package io.xxx.wcp.provider.web;

import io.xxx.wcp.provider.domain.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserInfo, String> {

    Optional<UserInfo> findByOpenid(String openid);
}
