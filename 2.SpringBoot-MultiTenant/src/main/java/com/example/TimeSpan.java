package com.example;

/**
 * @author tianc  
 */
public class TimeSpan implements Comparable<TimeSpan> {
	public final static long TicksPerMillisecond = 10000;
	public final static long TicksPerSecond = TicksPerMillisecond * 1000; // 10,000,000
	public final static long TicksPerMinute = TicksPerSecond * 60; // 600,000,000
	public final static long TicksPerHour = TicksPerMinute * 60; // 36,000,000,000
	public final static long TicksPerDay = TicksPerHour * 24; // 864,000,000,000
	private final static int MillisPerSecond = 1000;
	private final static int MillisPerMinute = MillisPerSecond * 60; // 60,000
	private final static int MillisPerHour = MillisPerMinute * 60; // 3,600,000
	private final static long MaxMilliSeconds = Long.MAX_VALUE / TicksPerMillisecond;
	private final static long MinMilliSeconds = Long.MIN_VALUE / TicksPerMillisecond;

	private final static long TicksPerTenthSecond = TicksPerMillisecond * 100;

	public final static TimeSpan Zero = new TimeSpan(0);

	public final static TimeSpan MaxValue = new TimeSpan(Long.MAX_VALUE);
	public final static TimeSpan MinValue = new TimeSpan(Long.MIN_VALUE);

	private long _ticks;

	public TimeSpan(long ticks) {
		this._ticks = ticks;
	}

	public TimeSpan(int hours, int minutes, int seconds) throws Exception {
		this(0, hours, minutes, seconds, 0);
	}

	public TimeSpan(int days, int hours, int minutes, int seconds) throws Exception {
		this(days, hours, minutes, seconds, 0);
	}

	public TimeSpan(int d, int h, int m, int s, int ms) {
		long totalMilliSeconds = ((long) d * 3600 * 24 + (long) h * 3600 + (long) m * 60 + s) * 1000 + ms;

		_ticks = (long) totalMilliSeconds * TicksPerMillisecond;
	}

	public int Days() {
		return (int) (_ticks / TicksPerDay);
	}

	public int Hours() {
		return (int) ((_ticks / TicksPerHour) % 24);
	}

	public int Minutes() {
		return (int) ((_ticks / TicksPerMinute) % 60);
	}

	public int Seconds() {
		return (int) ((_ticks / TicksPerSecond) % 60);
	}

	public int Milliseconds() {
		return (int) ((_ticks / TicksPerMillisecond) % 1000);
	}

	public long Ticks() {
		return _ticks;
	}

	public int TotalHours() {
		return (this.Days() * 24) + this.Hours();
	}

	public int TotalMinutes() {
		return (((this.Days() * 24) + this.Hours()) * 60) + this.Minutes();
	}

	public int TotalSeconds() {
		return (((((this.Days() * 24) + this.Hours()) * 60) + this.Minutes()) * 60) + this.Seconds();
	}

	public int TotalMilliseconds() {
		return ((((((this.Days() * 24) + this.Hours()) * 60) + this.Minutes()) * 60) + this.Seconds()) * 1000
				+ this.Milliseconds();
	}

	public TimeSpan Add(TimeSpan ts) throws Exception {
		long result = _ticks + ts._ticks;
		// Overflow if signs of operands was identical and result's
		// sign was opposite.
		// >> 63 gives the sign bit (either 64 1's or 64 0's).
		if ((_ticks >> 63 == ts._ticks >> 63) && (_ticks >> 63 != result >> 63))
			throw new Exception("Overflow TimeSpan Too Long");
		return new TimeSpan(result);
	}

	public TimeSpan Subtract(TimeSpan ts) throws Exception {
		long result = _ticks - ts._ticks;
		// Overflow if signs of operands was different and result's
		// sign was opposite from the first argument's sign.
		// >> 63 gives the sign bit (either 64 1's or 64 0's).
		if ((_ticks >> 63 != ts._ticks >> 63) && (_ticks >> 63 != result >> 63))
			throw new Exception("Overflow TimeSpan Too Long");
		return new TimeSpan(result);
	}

	public static long TimeToTicks(boolean positive, int days, int hours, int minutes, int seconds, int fraction) {

		long ticks = ((long) days * 3600 * 24 + (long) hours * 3600 + (long) minutes * 60 + seconds) * 1000;
		if (ticks > TimeSpan.MaxMilliSeconds || ticks < TimeSpan.MinMilliSeconds) {
			return 0;
		}

		// Normalize the fraction component
		//
		// string representation => (zeroes,num) => resultant fraction ticks
		// --------------------- ------------ ------------------------
		// ".9999999" => (0,9999999) => 9,999,999 ticks (same as constant maxFraction)
		// ".1" => (0,1) => 1,000,000 ticks
		// ".01" => (1,1) => 100,000 ticks
		// ".001" => (2,1) => 10,000 ticks
		long f = fraction;
		if (f != 0) {
			long lowerLimit = TimeSpan.TicksPerTenthSecond;
			if (fraction > 0) {
				long divisor = (long) Math.pow(10, fraction);
				lowerLimit = lowerLimit / divisor;
			}
			while (f < lowerLimit) {
				f *= 10;
			}
		}

		long result = ((long) ticks * TimeSpan.TicksPerMillisecond) + f;
		if (positive && result < 0) {
			return 0;
		}
		return result;
	}

	public static boolean equals(TimeSpan t1, TimeSpan t2) {
		return t1._ticks == t2._ticks;
	}
	
	@Override
	public String toString() {
		if (this.Days() != 0) {
			return String.format("%d:%02d:%02d:%02d:%02d", this.Days(), this.Hours(), this.Minutes(), this.Seconds(),
					this.Milliseconds());
		} else {
			return String.format("%02d:%02d:%02d:%02d", this.Hours(), this.Minutes(), this.Seconds(),
					this.Milliseconds());
		}
	}
	
	@Override
	public int compareTo(TimeSpan ts) {
		if (ts == null)
			return 1;

		long t = ts._ticks;
		if (_ticks > t)
			return 1;
		if (_ticks < t)
			return -1;
		return 0;
	}

	@Override
	public boolean equals(Object ts) {
		if (!(ts instanceof TimeSpan)) {
			return false;
		}
		return this._ticks == ((TimeSpan) ts)._ticks;
	}

	@Override
	public int hashCode() {
		return (int) _ticks ^ (int) (_ticks >> 32);
	}
}
