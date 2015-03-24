package com.yammer.metrics.reporting;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;

import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.VirtualMachineMetrics;
import com.yammer.metrics.core.VirtualMachineMetrics.GarbageCollectorStats;

public class JVMMetricsCollector {

	public static final JVMMetricsCollector INSTANCE = new JVMMetricsCollector(
			ManagementFactory.getMemoryMXBean(),
			ManagementFactory.getMemoryPoolMXBeans(),
			ManagementFactory.getOperatingSystemMXBean(),
			ManagementFactory.getThreadMXBean(),
			ManagementFactory.getGarbageCollectorMXBeans(),
			ManagementFactory.getRuntimeMXBean(),
			ManagementFactory.getPlatformMBeanServer());

	private final MemoryMXBean memory;
	private final List<MemoryPoolMXBean> memoryPools;
	private final OperatingSystemMXBean os;
	private final ThreadMXBean threads;
	private final List<GarbageCollectorMXBean> garbageCollectors;
	private final RuntimeMXBean runtime;
	private final MBeanServer mBeanServer;

	JVMMetricsCollector(MemoryMXBean memory,
			List<MemoryPoolMXBean> memoryPools, OperatingSystemMXBean os,
			ThreadMXBean threads,
			List<GarbageCollectorMXBean> garbageCollectors,
			RuntimeMXBean runtime, MBeanServer mBeanServer) {
		this.memory = memory;
		this.memoryPools = memoryPools;
		this.os = os;
		this.threads = threads;
		this.garbageCollectors = garbageCollectors;
		this.runtime = runtime;
		this.mBeanServer = mBeanServer;

	}

	public Number heapCommitted() {

		return memory.getHeapMemoryUsage().getCommitted();
	}

	public Number heapUsed() {

		return memory.getHeapMemoryUsage().getUsed();
	}

	public long daemonThreadCount() {
		return threads.getDaemonThreadCount();
	}

	public double getSystemLoadAverage() {
		return os.getSystemLoadAverage();
	}

	public Map<String, GarbageCollectorStats> garbageCollectors() {
		final Map<String, GarbageCollectorStats> stats = new HashMap<String, GarbageCollectorStats>();
		for (GarbageCollectorMXBean gc : garbageCollectors) {
			stats.put(
					gc.getName(),
					new GarbageCollectorStats(gc.getCollectionCount(), gc
							.getCollectionTime()));
		}
		return Collections.unmodifiableMap(stats);
	}

	public long getThreadCount() {
		return threads.getThreadCount();
	}

	/**
	 * Per-GC statistics.
	 */
	public static class GarbageCollectorStats {
		private final long runs, timeMS;

		private GarbageCollectorStats(long runs, long timeMS) {
			this.runs = runs;
			this.timeMS = timeMS;
		}

		/**
		 * Returns the number of times the garbage collector has run.
		 *
		 * @return the number of times the garbage collector has run
		 */
		public long getRuns() {
			return runs;
		}

		/**
		 * Returns the amount of time in the given unit the garbage collector
		 * has taken in total.
		 *
		 * @param unit
		 *            the time unit for the return value
		 * @return the amount of time in the given unit the garbage collector
		 */
		public long getTime(TimeUnit unit) {
			return unit.convert(timeMS, TimeUnit.MILLISECONDS);
		}
	}

}
