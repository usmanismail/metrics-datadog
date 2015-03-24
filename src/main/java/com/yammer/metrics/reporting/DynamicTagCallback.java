package com.yammer.metrics.reporting;

import java.util.Collection;

import com.yammer.metrics.core.MetricName;

public interface DynamicTagCallback {

	public Collection<? extends String> getDynamicTags(MetricName name);

}
