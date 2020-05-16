package com.alon.client;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface VolleyResultInterface {
    public void notifyError(VolleyError error);
    public void notifySuccessObject(JSONObject response);
    public void notifySuccessArray(JSONArray response);
}
