package ch.appquest.indiana_phones.appquest4_schatzkarte;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyLocationNewOverlay mLocationOverlay;
    private MapView map;
    private CompassOverlay mCompassOverlay;
    LocationManager mLocationManager;
    Context ctx;

    ArrayList<OverlayItem> points;
    ItemizedOverlayWithFocus<OverlayItem> pointOverlay;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pointFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        Location location;
        double longitude;
        double latitude;
        try {
            location = getLastKnownLocation();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            IMapController mapController = map.getController();
            mapController.setZoom(20);
            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint);
        } catch (SecurityException e) {} catch (Exception x) {}


        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        this.mLocationOverlay.enableMyLocation();
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.mypos);
        Bitmap bmp = RotateBitmap(drawable.getBitmap(), 180);
        this.mLocationOverlay.setDirectionArrow(bmp, bmp);
        this.mLocationOverlay.setPersonIcon(bmp);
        map.getOverlays().add(this.mLocationOverlay);

        this.mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        this.mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);

        pointFunctions = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                map.getOverlays().remove(pointOverlay);
                points.remove(item);
                pointOverlay = new ItemizedOverlayWithFocus<OverlayItem>(ctx, points, pointFunctions);
                pointOverlay.setFocusItemsOnTap(true);
                map.getOverlays().add(pointOverlay);
                Toast.makeText(ctx, "Point Removed",Toast.LENGTH_SHORT).show();
                saveAllPoints();
                return false;
            }
        };

        //your items
        points = new ArrayList<OverlayItem>();

        List<Point> pArray = new Position().getPoints();

        Log.d("TESTUNG", pArray.toArray(new Point[pArray.size()]).toString());

        for (Point p: pArray)
        {
            double microLat = (double)((double)p.x / (double)1000000);
            double microLong = (double)((double)p.y / (double)1000000);
            //Log.d("LATLONG", microLat + " - " + microLong);
            points.add(new OverlayItem("Latitude: " + microLat, "Longitude " + microLong, new GeoPoint(microLat,microLong)));
        }

        pointOverlay = new ItemizedOverlayWithFocus<OverlayItem>(ctx, points, pointFunctions);
        pointOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(pointOverlay);

        Overlay touchOverlay = new Overlay(this){
            @Override
            public void draw(Canvas c, MapView osmv, boolean shadow) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
                Projection proj = mapView.getProjection();
                IGeoPoint p = proj.fromPixels((int)e.getX(), (int)e.getY());
                map.getOverlays().remove(pointOverlay);
                points.add(new OverlayItem("Latitude: " + p.getLatitude(), "Longitude " + p.getLongitude(), new GeoPoint(p.getLatitude(),p.getLongitude())));
                pointOverlay = new ItemizedOverlayWithFocus<OverlayItem>(ctx, points, pointFunctions);
                pointOverlay.setFocusItemsOnTap(true);
                map.getOverlays().add(pointOverlay);
                Toast.makeText(ctx, "Point Added",Toast.LENGTH_SHORT).show();
                saveAllPoints();
                return false;
            }

            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;

        };
        map.getOverlays().add(touchOverlay);
    }

    private void saveAllPoints()
    {
        List<Point> pList = new ArrayList<Point>();
        for (OverlayItem point: points)
        {
            int microLat = (int)(point.getPoint().getLatitude() * 1000000);
            int microLong = (int)(point.getPoint().getLongitude() * 1000000);
            pList.add(new Point(microLat, microLong));
        }
        Point[] pArray = pList.toArray(new Point[pList.size()]);
        new Position(pArray);
    }

    private Location getLastKnownLocation() {
        try {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            return bestLocation;
        } catch (SecurityException e) {} catch (Exception x) {}
        return null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemLog = menu.add("Follow Me");
        menuItemLog.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mLocationOverlay.enableFollowLocation();
                return false;
            }
        });

        MenuItem menuItemLog2 = menu.add("Add Point Here");
        menuItemLog2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                map.getOverlays().remove(pointOverlay);
                points.add(new OverlayItem("Latitude: " + getLastKnownLocation().getLatitude(), "Longitude " + getLastKnownLocation().getLongitude(), new GeoPoint(getLastKnownLocation().getLatitude(),getLastKnownLocation().getLongitude())));
                pointOverlay = new ItemizedOverlayWithFocus<OverlayItem>(ctx, points, pointFunctions);
                pointOverlay.setFocusItemsOnTap(true);
                map.getOverlays().add(pointOverlay);
                Toast.makeText(ctx, "Point Added",Toast.LENGTH_SHORT).show();
                saveAllPoints();
                return false;
            }
        });

        MenuItem menuItemLog3 = menu.add("Log");
        menuItemLog3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ask_log();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void ask_log()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Abschicken");
        builder.setMessage("Wollen Sie die Lösung wirklich abschicken?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try
                        {
                            write_log();
                        }
                        catch(JSONException x)
                        {
                            Toast.makeText(MainActivity.this, x.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void write_log() throws JSONException {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("task", "Schatzkarte");
        String jString = "[";
        for (Point p: new Position().getPoints())
        {
            jString += "{'lat': "+p.x+", 'lon': " + p.y + "},";
        }
        jString = jString.substring(0, jString.length() - 1);
        jString += "]";
        json.put("points", jString);
        String logmessage = json.toString();
        logmessage = logmessage.replaceAll("\"", "");
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

}
