package io.xxx.wcp.provider.web;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import io.xxx.wcp.provider.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;

    public UserController(UserRepository userRepository,
                          MongoTemplate mongoTemplate,
                          MongoClient mongoClient) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
    }

    @GetMapping("/save")
    public Integer saveUsers(@RequestParam(required = false, defaultValue = "100000") Integer count) {
        long start = System.currentTimeMillis();

        int max = 100_000;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<UserInfo> userInfos = new ArrayList<>(max);
        for (int i = 1; i <= count; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUnionid("Unionid-" + i);
            userInfo.setOpenid("Openid-" + i);
            userInfo.setNickname("Nickname-" + i);
            userInfo.setSex(random.nextInt(2));
            userInfo.setLanguage("zh_CN");
            userInfo.setCity("城市-" + random.nextInt(1000));
            userInfo.setProvince("省份-" + random.nextInt(34));
            userInfo.setCountry("中国");
            userInfo.setHeadimgurl("http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0");
            userInfo.setSubscribeTime(System.currentTimeMillis() / 1000);
            userInfo.setRemark(null);
            userInfo.setGroupid(random.nextInt(100));
            userInfo.setTagidList(getTagIdList(random.nextInt(11), random));
            userInfo.setSubscribeScene(scenes[random.nextInt(scenes.length)]);
            userInfo.setSubscribe(1);
            userInfo.setQrScene(random.nextInt());
            userInfo.setQrSceneStr(null);
            userInfos.add(userInfo);
            if (i % max == 0) {
                userRepository.saveAll(userInfos);
                userInfos.clear();
                System.out.println("已插入" + (i / max) + " * 100,000条数据");
            }
        }

        return (int) ((System.currentTimeMillis() - start) / 1000);
    }

    private String[] scenes = new String[]{
            "ADD_SCENE_SEARCH", "ADD_SCENE_ACCOUNT_MIGRATION", "ADD_SCENE_PROFILE_CARD",
            "ADD_SCENE_QR_CODE", "ADD_SCENE_PROFILE_LINK", "ADD_SCENE_PROFILE_ITEM", "ADD_SCENE_PAID", "ADD_SCENE_OTHERS"
    };

    private List<Integer> getTagIdList(int size, Random random) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(200));
        }
        return list;
    }

    @GetMapping("/get")
    public Optional<UserInfo> getUserInfo(@RequestParam String openid) {
        return userRepository.findByOpenid(openid);
    }

    @GetMapping("/ids")
    public Map<String, Object> getIds(@PageableDefault(size = 1) Pageable pageable, @RequestParam(required = false) String nextOpennid) {
        Page<UserInfo> userInfos = userRepository.findAll(pageable);

        int pageSize = 10_000;
        DB db = mongoClient.getDB("test");
        DBCollection table = db.getCollection("user_info");
        long total = table.count();
        long pages = getTotalPages(total, pageSize);
        ObjectId lastIdObject = nextOpennid == null ? userInfos.getContent().get(0).getId()
                : userRepository.findByOpenid(nextOpennid).orElseThrow().getId();
        DBCursor dbObjects = getCursorForCollection(table, lastIdObject, pageSize);
        List<DBObject> objs = dbObjects.toArray();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("count", pageSize);
        result.put("data", objs.stream().map(dbObject -> dbObject.get("openid")).collect(Collectors.toList()));
        result.put("next_openid", objs.get(objs.size() - 1).get("openid"));
        return result;
    }

    private static DBCursor getCursorForCollection(DBCollection collection, ObjectId lastIdObject, int pageSize) {
        DBCursor dbObjects;
        if (lastIdObject == null) {
            //TODO 排序sort取第一个，否则可能丢失数据
            lastIdObject = (ObjectId) Objects.requireNonNull(collection.findOne()).get("_id");
        }
        BasicDBObject query = new BasicDBObject();
        query.append("_id", new BasicDBObject("$gt", lastIdObject));
        BasicDBObject sort = new BasicDBObject();
        sort.append("_id", 1);
        dbObjects = collection.find(query).limit(pageSize).sort(sort);
        return dbObjects;
    }

    private static long getTotalPages(long count, int pageSize) {
        return count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
    }

    @GetMapping("/large")
    public List<UserInfo> large(@PageableDefault Pageable pageable) {
        try {
            FindIterable<UserInfo> iterable = mongoTemplate.getCollection("user_info")
                    .find(UserInfo.class)
                    .sort(new BasicDBObject("_id", 1))
                    .limit(pageable.getPageSize());

            List<UserInfo> userInfos = new ArrayList<>();
            for (UserInfo userInfo : iterable) {
                userInfos.add(userInfo);
            }
            return userInfos;
        } catch (Exception e) {
            log.error("***大数据量数据分页查询失败,collectionName=user_info", e.getCause());
        }
        return null;
    }

    @GetMapping("/demo")
    public Object demo() {
        return mongoTemplate.getCollection("user_info").countDocuments();
    }

    @GetMapping("/large2")
    public Iterable<UserInfo> large2() {
        return new ArrayList<>() {{
            UserInfo userInfo = new UserInfo();
            add(userInfo);
        }};
    }
}
