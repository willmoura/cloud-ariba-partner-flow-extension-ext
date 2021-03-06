package com.sap.cloud.samples.ariba.partner.flow.api;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Class that provides Gson singleton instance. The Gson instance converts
 * single-object responses into an array containing the object whenever the
 * target type is array.
 *
 */
public class GsonProvider {

	private static volatile Gson gson;

	public static Gson getInstance() {
		if (GsonProvider.gson == null) {
			synchronized (GsonProvider.class) {
				if (GsonProvider.gson == null) {
					GsonProvider.gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory())
							.create();
				}
			}
		}

		return GsonProvider.gson;
	}
}

class ArrayAdapter<T> extends TypeAdapter<List<T>> {

	private Class<T> adapterclass;

	public ArrayAdapter(Class<T> adapterclass) {
		this.adapterclass = adapterclass;
	}

	@SuppressWarnings("unchecked")
	public List<T> read(JsonReader reader) throws IOException {
		List<T> list = new ArrayList<T>();
		Gson gson = new Gson();
		if (reader.peek() == JsonToken.BEGIN_OBJECT) {
			T inning = (T) gson.fromJson(reader, adapterclass);
			list.add(inning);
		} else if (reader.peek() == JsonToken.BEGIN_ARRAY) {
			reader.beginArray();
			while (reader.hasNext()) {
				T inning = (T) gson.fromJson(reader, adapterclass);
				list.add(inning);
			}
			reader.endArray();
		} else {
			reader.skipValue();
		}

		return list;
	}

	public void write(JsonWriter writer, List<T> value) throws IOException {
	}
}

class ArrayAdapterFactory implements TypeAdapterFactory {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
		ArrayAdapter typeAdapter = null;
		try {
			if (type.getRawType() == List.class) {
				typeAdapter = new ArrayAdapter(
						(Class) ((ParameterizedType) type.getType()).getActualTypeArguments()[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return typeAdapter;
	}
}
