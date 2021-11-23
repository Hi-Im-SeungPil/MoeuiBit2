package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// 비동기 처리를 위해 AsyncTask.
public class GetUpBitCoins extends AsyncTask<String,Void, JSONArray> {

    JSONArray jsonCoinInfo;

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected JSONArray doInBackground(String... strings) {

        try {
            URL url = new URL(strings[0]);
            // 웹 서버 HTTP 통신을 위해
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //inputStream 은 데이터를 byte 단위로 읽어들이는 통로(읽어드린 델이터를 byte로 돌려줌)
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            //bufferedReader 는 버퍼를 이용하여 ㅇ파일로부터 문자열을 읽을 때 사용
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer builder = new StringBuffer();

            String inputString = null;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }

            String s = builder.toString();
            jsonCoinInfo = new JSONArray(s);

            conn.disconnect();
            bufferedReader.close();
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonCoinInfo;
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled(); }

}
