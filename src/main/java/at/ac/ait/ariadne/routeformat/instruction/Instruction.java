package at.ac.ait.ariadne.routeformat.instruction;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import at.ac.ait.ariadne.routeformat.Validatable;
import at.ac.ait.ariadne.routeformat.geojson.CoordinatePoint;
import at.ac.ait.ariadne.routeformat.geojson.GeoJSONFeature;
import at.ac.ait.ariadne.routeformat.geojson.GeoJSONPoint;

/**
 * Common base class for all turn-by-turn navigation instructions
 * 
 * @author AIT Austrian Institute of Technology GmbH
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = BasicRoadInstruction.class, name = "BasicRoadInstruction"),
		@JsonSubTypes.Type(value = RoadCrossingInstruction.class, name = "RoadCrossingInstruction"),
		@JsonSubTypes.Type(value = RoundaboutInstruction.class, name = "RoundaboutInstruction") })
public abstract class Instruction<T extends Instruction<T>> implements Validatable {

	private GeoJSONFeature<GeoJSONPoint> position;
	private Optional<GeoJSONFeature<GeoJSONPoint>> previewTriggerPosition = Optional.empty();
	private Optional<GeoJSONFeature<GeoJSONPoint>> confirmationTriggerPosition = Optional.empty();
	private Map<String, String> text = Maps.newHashMap();
	private Map<String, Object> additionalInfo = Maps.newHashMap();

	// -- getters

	/**
	 * @return the position the instruction is defined for
	 */
	public GeoJSONFeature<GeoJSONPoint> getPosition() {
		return position;
	}

	/**
	 * @return a position where, during on-trip navigation, a first instruction
	 *         could be presented to the user
	 */
	public Optional<GeoJSONFeature<GeoJSONPoint>> getPreviewTriggerPosition() {
		return previewTriggerPosition;
	}

	/**
	 * @return a position after the maneuver, where, during on-trip navigation,
	 *         a confirmation message could be presented to the user
	 */
	public Optional<GeoJSONFeature<GeoJSONPoint>> getConfirmationTriggerPosition() {
		return confirmationTriggerPosition;
	}

	/**
	 * @return text versions of the instruction, the key of the map is the
	 *         language (RFC 1766), e.g. 'en' or 'de'.
	 */
	public Map<String, String> getText() {
		return text;
	}

	public Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}

	// -- setters

	@JsonProperty
	@SuppressWarnings("unchecked")
	public T setPosition(GeoJSONFeature<GeoJSONPoint> position) {
		this.position = position;
		return (T) this;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	public T setPosition(CoordinatePoint position) {
		this.position = GeoJSONFeature.newPointFeature(position);
		return (T) this;
	}

	@JsonProperty
	@SuppressWarnings("unchecked")
	public T setPreviewTriggerPosition(GeoJSONFeature<GeoJSONPoint> previewTriggerPosition) {
		this.previewTriggerPosition = Optional.ofNullable(previewTriggerPosition);
		return (T) this;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	public T setPreviewTriggerPosition(CoordinatePoint previewTriggerPosition) {
		this.previewTriggerPosition = Optional.ofNullable(GeoJSONFeature.newPointFeature(previewTriggerPosition));
		return (T) this;
	}

	@JsonProperty
	@SuppressWarnings("unchecked")
	public T setConfirmationTriggerPosition(GeoJSONFeature<GeoJSONPoint> confirmationTriggerPosition) {
		this.confirmationTriggerPosition = Optional.ofNullable(confirmationTriggerPosition);
		return (T) this;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	public T setConfirmationTriggerPosition(CoordinatePoint confirmationTriggerPosition) {
		this.confirmationTriggerPosition = Optional
				.ofNullable(GeoJSONFeature.newPointFeature(confirmationTriggerPosition));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setText(Map<String, String> text) {
		this.text = Maps.newHashMap(text);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setAdditionalInfo(Map<String, Object> additionalInfo) {
		this.additionalInfo = Maps.newHashMap(additionalInfo);
		return (T) this;
	}

	@Override
	public void validate() {
		Preconditions.checkArgument(position != null, "position is mandatory but missing");
	}

	@Override
	public String toString() {
		return "Instruction [position=" + position + ", previewTriggerPosition=" + previewTriggerPosition
				+ ", confirmationTriggerPosition=" + confirmationTriggerPosition + ", text=" + text
				+ ", additionalInfo=" + additionalInfo + "]";
	}

}
