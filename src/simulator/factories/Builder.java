package simulator.factories;

import org.json.JSONObject;

public abstract class Builder<T> {
	
	protected String _typeTag;
	protected String _desc;
	protected JSONObject _data;
	
	public Builder(String typeTag, String desc) throws IllegalArgumentException{
		
		if(typeTag == null || desc == null || typeTag.trim().length() <= 0 || desc.trim().length() <= 0)
			throw new IllegalArgumentException();
		else {
			
			this._typeTag = typeTag;
			this._desc = desc;
		}
	}
	
	String getTypeTag() {return this._typeTag;}
	
	public JSONObject getInfo() {
		
		JSONObject info = new JSONObject();
		info.put("type", _typeTag);
		info.put("desc", _desc);
		
		info.put("data", _data);
		
		return info;
	}
	
	@Override
	public String toString() {return _desc;}
	
	protected abstract T createInstance(JSONObject data);
}
