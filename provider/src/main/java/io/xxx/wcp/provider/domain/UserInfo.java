package io.xxx.wcp.provider.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_info")
@Data
public class UserInfo {

    /**
     * subscribe : 1
     * openid : o6_bmjrPTlm6_2sgVt7hMZOPfL2M
     * nickname : Band
     * sex : 1
     * language : zh_CN
     * city : 广州
     * province : 广东
     * country : 中国
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0
     * subscribeTime : 1382694957
     * unionid :  o6_bmasdasdsad6_2sgVt7hMZOPfL
     * remark :
     * groupid : 0
     * tagidList : [128,2]
     * subscribeScene : ADD_SCENE_QR_CODE
     * qrScene : 98765
     * qrSceneStr :
     */

    @Id
    private ObjectId id;
    private String unionid;
    private String openid;
    private String nickname;
    private int subscribe;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private long subscribeTime;
    private String remark;
    private int groupid;
    private String subscribeScene;
    private int qrScene;
    private String qrSceneStr;
    private List<Integer> tagidList;
}
