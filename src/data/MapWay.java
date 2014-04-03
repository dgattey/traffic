package data;

import java.util.Objects;

import server.graph.Weighable;

public class MapWay implements Weighable<MapWay>, Convertible<MapWay> {
	
	private final String	name;
	private final String	id;
	private final MapNode	start;
	private final MapNode	end;
	
	public static MapWay create(final String name, final String id, final MapNode start, final MapNode end) {
		if (name == null || id == null || start == null || end == null) {
			return null;
		}
		return new MapWay(name, id, start, end);
	}
	
	private MapWay(final String name, final String id, final MapNode start, final MapNode end) {
		this.name = name;
		this.id = id;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public double getWeight() {
		return start.distanceFrom(end);
	}
	
	public MapNode getStart() {
		return start;
	}
	
	public MapNode getEnd() {
		return end;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof MapWay)) {
			return false;
		}
		final MapWay o = (MapWay) other;
		return (id.equals(o.id));
		
	}
	
	@Override
	public String toString() {
		return String.format("%s -> %s : %s", start.getID(), end.getID(), name);
	}
	
	@Override
	public String encodeObject() {
		final String encoded = String.format("%s\n%s\n%s%s", name, id, start.encodeObject(), end.encodeObject());
		return encoded;
	}
	
	@Override
	public MapWay decodeObject(final String rep) {
		return null;
	}
}
