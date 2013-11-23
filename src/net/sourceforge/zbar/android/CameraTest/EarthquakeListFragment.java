package net.sourceforge.zbar.android.CameraTest;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.immersion.uhl.Launcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



import android.location.Location;
import android.os.Bundle;
import android.util.Log;


public class EarthquakeListFragment extends ListFragment  {
  
  ArrayAdapter<Quake> aa;
  ArrayList<Quake> earthquakes = new ArrayList<Quake>();
  protected Launcher mLauncher;
    private AdapterView.OnItemSelectedListener listener;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

      try {
          mLauncher = new Launcher(this.getActivity());
      } catch (Exception e) {
          Log.e("Haptic", "Exception!: " + e.getMessage());
      }
    int layoutID = android.R.layout.simple_list_item_1;
    aa = new ArrayAdapter<Quake>(getActivity(), layoutID , earthquakes);
    setListAdapter(aa);

    refreshEarthquakes();
  }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earthquake,container, false);





        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AdapterView.OnItemSelectedListener) {
            listener = (AdapterView.OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet MyListFragment.OnItemSelectedListener");
        }
    }

  public void refreshEarthquakes() {
    // Get the XML
    URL url;
    try {
      String quakeFeed = getString(R.string.quake_feed);
      url = new URL(quakeFeed);
        //http://earthquake.usgs.gov/earthquakes/feed/v0.1/summary/2.5_day.atom
        //http://earthquake.usgs.gov/earthquakes/catalogs/1day-M2.5.xml
      URLConnection connection;
      connection = url.openConnection();

      HttpURLConnection httpConnection = (HttpURLConnection)connection;
      int responseCode = httpConnection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        InputStream in = httpConnection.getInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Parse the earthquake feed.
        Document dom = db.parse(in);
        Element docEle = dom.getDocumentElement();

        // Clear the old earthquakes
        earthquakes.clear();

        // Get a list of each earthquake entry.
        NodeList nl = docEle.getElementsByTagName("entry");
        if (nl != null && nl.getLength() > 0) {
          for (int i = 1 ; i < nl.getLength(); i++) {
            Element entry = (Element)nl.item(i);
            Element title = (Element)entry.getElementsByTagName("title").item(0);
            Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
            Element when = (Element)entry.getElementsByTagName("updated").item(0);
            Element link = (Element)entry.getElementsByTagName("link").item(0);

            String details = title.getFirstChild().getNodeValue();
            String hostname = "http://earthquake.usgs.gov";
            String linkString = hostname + link.getAttribute("href");

            String point = g.getFirstChild().getNodeValue();
            String dt = when.getFirstChild().getNodeValue(); 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            Date qdate = new GregorianCalendar(0,0,0).getTime();
            try {
              qdate = sdf.parse(dt);
            } catch (ParseException e) {
              Log.d(getTag(), "Date parsing exception.", e);
            }

            String[] location = point.split(" ");
            Location l = new Location("dummyGPS");
            l.setLatitude(Double.parseDouble(location[0]));
            l.setLongitude(Double.parseDouble(location[1]));

            String magnitudeString = details.split(" ")[1];
            int end =  magnitudeString.length()-1;
            double magnitude = Double.parseDouble(magnitudeString.substring(0, end));

            details = details.split(",")[1].trim();

            Quake quake = new Quake(qdate, details, l, magnitude, linkString);

            // Process a newly found earthquake
            addNewQuake(quake);
              try {
                  mLauncher.play(Launcher.SHORT_TRANSITION_RAMP_UP_100);

              } catch (Exception e) {

              }
          }
        }
      }
    } catch (MalformedURLException e) {
      Log.d(getTag(), "MalformedURLException", e);
    } catch (IOException e) {
      Log.d(getTag(), "IOException", e);
    } catch (ParserConfigurationException e) {
      Log.d(getTag(), "Parser Configuration Exception", e);
    } catch (SAXException e) {
      Log.d(getTag(), "SAX Exception", e);
    }
    finally {
    }
  }

  private void addNewQuake(Quake _quake) {
    // Add the new quake to our list of earthquakes.
    earthquakes.add(_quake);

    // Notify the array adapter of a change.
    aa.notifyDataSetChanged();
  }
    public boolean onKeyDown(int keyCode, KeyEvent msg){
        if((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME)
                || (keyCode == KeyEvent.KEYCODE_CALL))
            return false;
        else
            return true;
    }


}