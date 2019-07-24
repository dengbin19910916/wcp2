package io.xxx.wcp.consumer;

public interface UserStorage {

    void save(UserInfo userInfo);

    void remove(UserInfo userInfo);
}
