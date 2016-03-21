package at.ac.ait.ariadne.routeformat.geojson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A feature collection restricted to features of just one type
 * 
 * @author AIT Austrian Institute of Technology GmbH
 */
@JsonInclude(Include.ALWAYS)
public class GeoJSONFeatureCollection<T extends GeoJSONGeometryObject> {

	@JsonProperty(required = true)
	public final GeoJSONType type = GeoJSONType.FeatureCollection;

	@JsonProperty(required = false)
	public CRS crs = CRS.WGS84;

	@JsonProperty(required = true)
	public List<GeoJSONFeature<T>> features = new ArrayList<>();

	public List<String> toWKT() {
		return features.stream().map(f -> f.toWKT()).collect(Collectors.toList());
	}

	public GeoJSONFeatureCollection() {
	}

}