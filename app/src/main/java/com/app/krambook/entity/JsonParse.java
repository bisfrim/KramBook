package com.app.krambook.entity;

import android.util.Log;

import com.app.krambook.models.SuggestGetSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonParse {
    double current_latitude, current_longitude;

    public JsonParse() {
    }

    public JsonParse(double current_latitude, double current_longitude) {
        this.current_latitude = current_latitude;
        this.current_longitude = current_longitude;
    }

    public List<SuggestGetSet> getParseJsonWCF(String sName) {
        List<SuggestGetSet> ListData = new ArrayList<SuggestGetSet>();
        try {
            String temp = sName.replace(" ", "%20");
            String result = "";
            //https://jsonblob.com/api/jsonBlob/db826bae-1ede-11e7-a0ba-095f802ac102?name=
            //http://universities.hipolabs.com/search?name=
            //https://api.data.gov/ed/collegescorecard/v1/schools?api_key=Ndy48AXCSG33S0QgRy3M6m993o8tfv3E4Xr2mwDi&school.name=
            URL js = new URL("http://universities.hipolabs.com/search?name=" + temp);
            HttpURLConnection jc = (HttpURLConnection) js.openConnection();
            jc.setRequestMethod("GET");

            if(jc.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d("JSONParse", "Error could not reach server" + js.getHost());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            //String line = reader.readLine();
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            result = sb.toString();


            Log.e("JSONParse", "Response from url: " + result);
            //JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject r = jsonArray.getJSONObject(i);
                ListData.add(new SuggestGetSet(r.getString("domain"),r.getString("name")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;

    }

}
