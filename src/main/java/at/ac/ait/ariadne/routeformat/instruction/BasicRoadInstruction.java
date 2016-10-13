package at.ac.ait.ariadne.routeformat.instruction;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedMap;

import at.ac.ait.ariadne.routeformat.Constants.CompassDirection;
import at.ac.ait.ariadne.routeformat.Constants.FormOfWay;
import at.ac.ait.ariadne.routeformat.Constants.TurnDirection;
import at.ac.ait.ariadne.routeformat.geojson.CoordinatePoint;
import at.ac.ait.ariadne.routeformat.geojson.GeoJSONFeature;
import at.ac.ait.ariadne.routeformat.geojson.GeoJSONPoint;
import at.ac.ait.ariadne.routeformat.instruction.BasicRoadInstruction.Builder;

/**
 * A {@link BasicRoadInstruction} contains episodes with classic-style turn
 * navigations for street-based modes of transport such as walking, cycling and
 * driving (keep straight, turn left/right, make a u-turn). Most fields are
 * optional, but at least one of {@link #ontoStreetName} and
 * {@link #ontoFormOfWay} is guaranteed to be available.
 * <p>
 * Exemplary EBNF of how this instruction can be transformed into human-readable
 * text and what's mandatory / optional. Elements ending with STRING are
 * terminal (not defined any further).
 * <p>
 * CONTINUE_LANDMARK_STRING must be retrieved from the next {@link Instruction},
 * it can be a classic landmark or also the type of the next instruction, e.g.
 * roundabout.
 * 
 * <pre>
 * {@code
 * BASIC_INSTRUCTION = ROUTE_START | ROUTE_END | STRAIGHT | TURN | U_TURN;
 * 
 * ROUTE_START = "Start", [LANDMARK_PART], "on", NAME_OR_TYPE, [INITIAL_DIRECTION], [CONFIRMATION_LANDMARK_PART], [CONTINUE];
 * ROUTE_END = "You reached your destination", [LANDMARK_PART], "on", NAME_OR_TYPE;
 * STRAIGHT = "Keep straight", [LANDMARK_PART], "on", NAME_OR_TYPE, [CONFIRMATION_LANDMARK_PART], [CONTINUE];
 * TURN = "Turn", ["slight"], DIRECTION, [LANDMARK_PART], "on", NAME_OR_TYPE, [CONFIRMATION_LANDMARK_PART], [CONTINUE];
 * U_TURN = "Make a u-turn", [LANDMARK_PART], on", NAME_OR_TYPE, [CONFIRMATION_LANDMARK_PART], [CONTINUE];
 * 
 * NAME_OR_TYPE = STREET_NAME_STRING | FORM_OF_WAY_STRING;
 * 
 * INITIAL_DIRECTION = "heading", COMPASS_STRING, ["into the direction of", CONTINUE_LANDMARK_STRING];
 *
 * CONTINUE = "and follow it", ["for", UNIT], ["until", CONTINUE_LANDMARK_PART]; (* at least one of the two *)
 * UNIT = [DISTANCE_STRING], [TIME_STRING]; (* at least one of the two *)
 * 
 * LANDMARK_PART = PREPOSITION, LANDMARK_STRING;
 * CONFIRMATION_LANDMARK_PART = CONFIRMATION_PREPOSITION, CONFIRMATION_LANDMARK_STRING;
 * CONTINUE_LANDMARK_PART = PREPOSITION, CONTINUE_LANDMARK_STRING;
 * 
 * PREPOSITION = "before" | "at" | "after";
 * CONFIRMATION_PREPOSITION = "towards" | "through" | "along" | "past";
 * 
 * DIRECTION = "left" | "right";
 * }
 * </pre>
 * 
 * @author AIT Austrian Institute of Technology GmbH
 */
@JsonDeserialize(builder = Builder.class)
@JsonInclude(Include.NON_EMPTY)
public class BasicRoadInstruction extends Instruction {

    public enum SubType {
        ROUTE_START, ROUTE_END, STRAIGHT, TURN, U_TURN
    }

    private final SubType subType;
    private final Optional<TurnDirection> turnDirection;
    private final Optional<CompassDirection> compassDirection;
    private final Optional<Boolean> roadChange;
    private final Optional<String> ontoStreetName;
    private final Optional<FormOfWay> ontoFormOfWay;
    private final Optional<Integer> continueMeters, continueSeconds;
    private final Optional<Landmark> landmark, confirmationLandmark;

    public SubType getSubType() {
        return subType;
    }

    /**
     * @return the turn direction relative to the direction until this point
     */
    public Optional<TurnDirection> getTurnDirection() {
        return turnDirection;
    }

    /**
     * @return the heading after this point
     */
    public Optional<CompassDirection> getCompassDirection() {
        return compassDirection;
    }

    /** @return <code>true</code> if the road name or type has changed */
    public Optional<Boolean> getRoadChange() {
        return roadChange;
    }

    public Optional<String> getOntoStreetName() {
        return ontoStreetName;
    }

    public Optional<FormOfWay> getOntoFormOfWay() {
        return ontoFormOfWay;
    }

    public Optional<Integer> getContinueMeters() {
        return continueMeters;
    }

    public Optional<Integer> getContinueSeconds() {
        return continueSeconds;
    }

    /**
     * @return the landmark at begin of the instruction, i.e. at the turn, or at
     *         the begin (for {@link SubType#ROUTE_START}) or at the end (for
     *         {@link SubType#ROUTE_END}) of the route. At the same time this
     *         landmark is the continue-landmark for the previous instruction,
     *         i.e. the landmark after {@link #getContinueMeters()}.
     */
    public Optional<Landmark> getLandmark() {
        return landmark;
    }

    /**
     * @return a landmark between this and the next instruction (or a global
     *         landmark in the general direction after this instruction) that
     *         helps users to stay on track
     */
    public Optional<Landmark> getConfirmationLandmark() {
        return landmark;
    }

    private BasicRoadInstruction(Builder builder) {
        super(builder.position, builder.previewTriggerPosition, builder.confirmationTriggerPosition, builder.text,
                builder.additionalInfo);
        this.subType = builder.subType;
        this.turnDirection = builder.turnDirection;
        this.compassDirection = builder.compassDirection;
        this.roadChange = builder.roadChange;
        this.ontoStreetName = builder.ontoStreetName;
        this.ontoFormOfWay = builder.ontoFormOfWay;
        this.continueMeters = builder.continueMeters;
        this.continueSeconds = builder.continueSeconds;
        this.landmark = builder.landmark;
        this.confirmationLandmark = builder.confirmationLandmark;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "BasicRoadInstruction [subType=" + subType + ", turnDirection=" + turnDirection + ", compassDirection="
                + compassDirection + ", roadChange=" + roadChange + ", ontoStreetName=" + ontoStreetName
                + ", ontoFormOfWay=" + ontoFormOfWay + ", continueMeters=" + continueMeters + ", continueSeconds="
                + continueSeconds + ", landmark=" + landmark + ", confirmationLandmark=" + confirmationLandmark + "]";
    }

    public static class Builder {
        private SubType subType;
        private GeoJSONFeature<GeoJSONPoint> position;
        private Optional<GeoJSONFeature<GeoJSONPoint>> previewTriggerPosition;
        private Optional<GeoJSONFeature<GeoJSONPoint>> confirmationTriggerPosition;
        private Map<String, String> text = Collections.emptyMap();
        private Map<String, Object> additionalInfo = Collections.emptyMap();
        private Optional<TurnDirection> turnDirection = Optional.empty();
        private Optional<CompassDirection> compassDirection = Optional.empty();
        private Optional<Boolean> roadChange = Optional.empty();
        private Optional<String> ontoStreetName = Optional.empty();
        private Optional<FormOfWay> ontoFormOfWay = Optional.empty();
        private Optional<Integer> continueMeters = Optional.empty(), continueSeconds = Optional.empty();;
        private Optional<Landmark> landmark = Optional.empty(), confirmationLandmark = Optional.empty();

        public Builder withSubType(SubType subType) {
            this.subType = subType;
            return this;
        }

        public Builder withPosition(GeoJSONFeature<GeoJSONPoint> position) {
            this.position = position;
            return this;
        }

        public Builder withPreviewTriggerPosition(GeoJSONFeature<GeoJSONPoint> previewTriggerPosition) {
            this.previewTriggerPosition = Optional.ofNullable(previewTriggerPosition);
            return this;
        }

        public Builder withConfirmationTriggerPosition(GeoJSONFeature<GeoJSONPoint> confirmationTriggerPosition) {
            this.confirmationTriggerPosition = Optional.ofNullable(confirmationTriggerPosition);
            return this;
        }

        public Builder withText(Map<String, String> text) {
            this.text = ImmutableSortedMap.copyOf(text);
            return this;
        }

        public Builder withAdditionalInfo(Map<String, Object> additionalInfo) {
            this.additionalInfo = ImmutableSortedMap.copyOf(additionalInfo);
            return this;
        }

        public Builder withTurnDirection(TurnDirection turnDirection) {
            this.turnDirection = Optional.ofNullable(turnDirection);
            return this;
        }

        public Builder withCompassDirection(CompassDirection compassDirection) {
            this.compassDirection = Optional.ofNullable(compassDirection);
            return this;
        }

        public Builder withRoadChange(Boolean roadChange) {
            this.roadChange = Optional.ofNullable(roadChange);
            return this;
        }

        public Builder withOntoStreetName(String ontoStreetName) {
            this.ontoStreetName = Optional.ofNullable(ontoStreetName);
            return this;
        }

        public Builder withOntoFormOfWay(FormOfWay ontoFormOfWay) {
            this.ontoFormOfWay = Optional.ofNullable(ontoFormOfWay);
            return this;
        }

        public Builder withContinueMeters(Integer continueMeters) {
            this.continueMeters = Optional.ofNullable(continueMeters);
            return this;
        }

        public Builder withContinueSeconds(Integer continueSeconds) {
            this.continueSeconds = Optional.ofNullable(continueSeconds);
            return this;
        }

        public Builder withLandmark(Landmark landmark) {
            this.landmark = Optional.ofNullable(landmark);
            return this;
        }

        public Builder withConfirmationLandmark(Landmark confirmationLandmark) {
            this.confirmationLandmark = Optional.ofNullable(confirmationLandmark);
            return this;
        }

        /**
         * Set all attributes useful for a {@link SubType#ROUTE_START}
         * 
         * @param compassDirection
         *            the direction in which the route is starting
         * @param landmark
         *            the landmark where the route starts
         */
        public Builder forRouteStart(CoordinatePoint position, CompassDirection compassDirection,
                Optional<String> ontoStreetName, Optional<FormOfWay> ontoFormOfWay, Optional<Integer> continueMeters,
                Optional<Integer> continueSeconds, Optional<Landmark> landmark,
                Optional<Landmark> confirmationLandmark) {
            this.subType = SubType.ROUTE_START;
            this.position = GeoJSONFeature.newPointFeature(position);
            this.compassDirection = Optional.of(compassDirection);
            this.ontoStreetName = ontoStreetName;
            this.ontoFormOfWay = ontoFormOfWay;
            this.continueMeters = continueMeters;
            this.continueSeconds = continueSeconds;
            this.landmark = landmark;
            this.confirmationLandmark = confirmationLandmark;
            return this;
        }

        /**
         * Set all attributes useful for an instruction for going straight and
         * turning
         */
        public Builder forNormalInstruction(CoordinatePoint position, TurnDirection turnDirection, boolean roadChange,
                Optional<String> ontoStreetName, Optional<FormOfWay> ontoFormOfWay, Optional<Integer> continueMeters,
                Optional<Integer> continueSeconds, Optional<Landmark> landmark,
                Optional<Landmark> confirmationLandmark) {
            this.subType = getSubType(turnDirection);
            this.position = GeoJSONFeature.newPointFeature(position);
            this.turnDirection = Optional.of(turnDirection);
            this.roadChange = Optional.of(roadChange);
            this.ontoStreetName = ontoStreetName;
            this.ontoFormOfWay = ontoFormOfWay;
            this.continueMeters = continueMeters;
            this.continueSeconds = continueSeconds;
            this.landmark = landmark;
            this.confirmationLandmark = confirmationLandmark;
            return this;
        }

        /**
         * Set all attributes useful for a {@link SubType#ROUTE_END}
         * 
         * @param landmark
         *            the landmark where the route ends
         */
        public Builder forRouteEnd(CoordinatePoint position, Optional<String> ontoStreetName,
                Optional<FormOfWay> ontoFormOfWay, Optional<Landmark> landmark) {
            this.subType = SubType.ROUTE_END;
            this.position = GeoJSONFeature.newPointFeature(position);
            this.ontoStreetName = ontoStreetName;
            this.ontoFormOfWay = ontoFormOfWay;
            this.landmark = landmark;
            return this;
        }

        public BasicRoadInstruction build() {
            validate();
            return new BasicRoadInstruction(this);
        }

        private void validate() {
            Preconditions.checkArgument(subType != null, "subType is mandatory but missing");
            Preconditions.checkArgument(position != null, "position is mandatory but missing");
            Preconditions.checkArgument(ontoStreetName.isPresent() || ontoFormOfWay.isPresent(),
                    "at least one onto-type is required");
        }
    }

    private static SubType getSubType(TurnDirection turnDirection) {
        if (turnDirection == TurnDirection.U_TURN)
            return SubType.U_TURN;
        else if (turnDirection == TurnDirection.STRAIGHT)
            return SubType.STRAIGHT;
        else
            return SubType.TURN;
    }

}
