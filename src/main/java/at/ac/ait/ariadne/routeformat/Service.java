package at.ac.ait.ariadne.routeformat;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import at.ac.ait.ariadne.routeformat.Service.Builder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * A {@link Service} typically represents a public transport service / line
 * 
 * @author AIT Austrian Institute of Technology GmbH
 */
@JsonDeserialize(builder = Builder.class)
@JsonInclude(Include.NON_EMPTY)
public class Service {
	private final String name;
	private final Optional<String> towards;
	private final Optional<String> color;
	private final Map<String, Object> additionalInfo;

	/**
	 * @return the official name of the service such as the line name (e.g. U3 for a subway line) to be provided to the
	 *         user
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the destination of the line as noted on the headsign of the vehicle
	 */
	public Optional<String> getTowards() {
		return towards;
	}

	/**
	 * @return the color of the line used by the public transport operator (e.g. red for U1 in Vienna) in hash-prepended
	 *         six-digit hexadacimal notation (e.g. #FF0000)
	 */
	public Optional<String> getColor() {
		return color;
	}

	public Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}

	public Service(Builder builder) {
		this.name = builder.name;
		this.towards = builder.towards;
		this.color = builder.color;
		this.additionalInfo = builder.additionalInfo;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private Optional<String> towards = Optional.empty();
		private Optional<String> color = Optional.empty();
		private Map<String, Object> additionalInfo = Collections.emptyMap();

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withTowards(String towards) {
			this.towards = Optional.ofNullable(towards);
			return this;
		}

		public Builder withColor(String color) {
			this.color = Optional.ofNullable(color);
			return this;
		}

		public Builder withAdditionalInfo(Map<String, Object> additionalInfo) {
			this.additionalInfo = ImmutableMap.copyOf(additionalInfo);
			return this;
		}

		public Service build() {
			validate();
			return new Service(this);
		}

		private void validate() {
			Preconditions.checkArgument(name != null, "name is mandatory but missing");
			if (color.isPresent()) {
				String colorStr = color.get();
				String error = "color must be represented as hash-prepended six-digit hexadecimal String but was %s";
				Preconditions.checkArgument(colorStr.startsWith("#"), error, colorStr);
				Preconditions.checkArgument(colorStr.length() == 7, error, colorStr);
				try {
					Long.parseLong(colorStr.substring(1, 7), 16);
				} catch (NumberFormatException e) {
					Preconditions.checkArgument(false, error, colorStr);
				}
			}
		}
	}

}