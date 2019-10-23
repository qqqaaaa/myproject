package com.microcyber.cloud.history.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
/**
 *
 * @author zhaohongwei
 */

@Configuration
public class PropsConfig {

    @Value("${kafka.consumer.servers}")
    private String broker;

    @Value("${kafka.consumer.groupId}")
    private String groupId;

    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    @Value("${kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;
    @Value("${kafka.consumer.session-timeout}")
    private String sessionTimeout;
    @Value("${kafka.consumer.max-poll-records}")
    private String maxPollRecords;
	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAutoOffsetReset() {
		return autoOffsetReset;
	}

	public void setAutoOffsetReset(String autoOffsetReset) {
		this.autoOffsetReset = autoOffsetReset;
	}

	public String getEnableAutoCommit() {
		return enableAutoCommit;
	}

	public void setEnableAutoCommit(String enableAutoCommit) {
		this.enableAutoCommit = enableAutoCommit;
	}

	public String getAutoCommitInterval() {
		return autoCommitInterval;
	}

	public void setAutoCommitInterval(String autoCommitInterval) {
		this.autoCommitInterval = autoCommitInterval;
	}

	public String getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getMaxPollRecords() {
		return maxPollRecords;
	}

	public void setMaxPollRecords(String maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

}
