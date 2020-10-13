package com.mnadeem.redis.service.pubsub;

public interface MessagePublisher {

    void publish(final String message);
}
