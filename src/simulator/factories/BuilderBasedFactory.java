package simulator.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	

	private Map<String,Builder<T>> _builders;
	private List<JSONObject> _buildersInfo;
	
	public BuilderBasedFactory() {
		// Create a HashMap for _builders, a LinkedList _buildersInfo
		// ...
		
		_builders = new HashMap<String, Builder<T>>();
		_buildersInfo = new ArrayList<JSONObject>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		// call addBuilder(b) for each builder b in builder
		// ...
		
		for(Builder<T> b : builders) {
			
			_builders.put(b.getTypeTag(), b);
			_buildersInfo.add(b.getInfo());
		}
	}
	
	public void addBuilder(Builder<T> b) throws IllegalArgumentException{
		// add and entry ‘‘ b.getTag() −> b’’ to _builders.
		// ...
		// add b.getInfo () to _buildersInfo
		// ...
		
		if (b == null)
			throw new IllegalArgumentException();
		else {
			_builders.put(b.getTypeTag(), b);
			_buildersInfo.add(b.getInfo());
		}
	}
	
	@Override
	public T createInstance(JSONObject info) throws IllegalArgumentException{
		
		boolean eq = false;
		T Object = null;
		
		if (info == null) {
			throw new IllegalArgumentException("Invalid value for createInstance: null");
		}
		else {
		// Search for a builder with a tag equals to info . getString("type"), call its
		// createInstance method and return the result if it is not null . The value you
		// pass to createInstance is :
		//
		// info . has("data") ? info . getJSONObject("data") : new getJSONObject()
		// If no builder is found or thr result is null ...
		
			for(Builder<T> b : _builders.values()) {
				
				if(b.getTypeTag().equals(info.getString("type"))) {
					if(info.has("data")) {
						Object = b.createInstance(info.getJSONObject("data"));
						eq = true;
					}
					else {
						Object = b.createInstance(info);
						eq = true;
					}
				}
			}
			
			if(!eq)
			throw new IllegalArgumentException("Invalid value for createInstance: " +
					info.toString());
		}
		
		return Object;
	}
		
	@Override
	public List<JSONObject> getInfo() {
		return Collections.unmodifiableList(_buildersInfo);
	}
}