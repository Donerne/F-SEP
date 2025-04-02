package com.example.f_sep;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Paint;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.os.AsyncTask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.util.PixelUtils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class forecast_report_3_weeks extends AppCompatActivity {

    private XYPlot xyPlot;
    private S3Utils s3Utils;
    private TextView valueOne;
    private TextView valueTwo;
    private TextView valueThree;
    private TextView percentOne;
    private TextView percentTwo;
    private TextView percentThree;

    private TextView downloadView;

    private static final String accessKey = BuildConfig.ACCESS_KEY;
    private static final String bucketName = BuildConfig.BUCKET_NAME;
    private static final String secretKey = BuildConfig.SECRET_KEY;

    private static final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forecast_report3_weeks);

        // Obtaining data from intent
        String company = getIntent().getStringExtra("Selected_Company");
        String ticker = null;
        String key = null;
        String companyName = null;

        if (company != null) {
            // Processing intent data by splitting name and ticker
            String[] parts = company.split(", ");
            companyName = parts[0];
            ticker = parts[1];
        }

        ImageView backButton = findViewById(R.id.backButton);
        TextView companyTextView = findViewById(R.id.companyForecastTextView);
        valueOne = findViewById(R.id.valueTextOne);
        valueTwo = findViewById(R.id.valueTextTwo);
        valueThree = findViewById(R.id.valueTextThree);
        percentOne = findViewById(R.id.percentageTextOne);
        percentTwo = findViewById(R.id.percentageTextTwo);
        percentThree = findViewById(R.id.percentageTextThree);
        downloadView = findViewById(R.id.threeForecastReport);
        xyPlot = findViewById(R.id.xyPlot);


        backButton.setOnClickListener(v -> finish());

        // Define the S3 object key
        key = "forecast_data/" + ticker + "_forecast" + ".csv";
        System.out.println(key);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        companyTextView.setText(companyName + " 3 Weeks Forecast");

        // Initialize S3Utils once
        s3Utils = new S3Utils(accessKey, secretKey);

        // Fetching and plotting data
        new FetchAndPlotTask().execute(bucketName, key, accessKey, secretKey);

//        downloadView.setOnClickListener(view -> {
//            if (ContextCompat.checkSelfPermission(forecast_report_3_weeks.this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(forecast_report_3_weeks.this,
//                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                // Request the permissions
//                ActivityCompat.requestPermissions(forecast_report_3_weeks.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                        STORAGE_PERMISSION_CODE);
//            } else {
//                // Permission already granted, proceed with your task
//                captureScreenAndSavePDF();
//            }
//        });

        downloadView.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11 (API 30) and above
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    captureScreenAndSavePDF();
                } else {
                    captureScreenAndSavePDF();
                }
            } else {
                // Android 6 to 12: Request old storage permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            }
        });
    }

    private void captureScreenAndSavePDF() {
        View rootView = getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Reports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        File pdfFile = new File(pdfDir, "Forecast_Report.pdf");

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(this, "PDF saved at: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                captureScreenAndSavePDF();
            } else {
                // Permission denied
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchAndPlotTask extends AsyncTask<String, Void, List<String[]>> {
        @Override
        protected List<String[]> doInBackground(String... params) {
            try {

                return s3Utils.fetchCsvFromS3(params[1]);

            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String[]> csvData) {
            if (csvData == null || csvData.isEmpty()) {
                System.out.println("CSV data is empty or null!"); // Debugging
                return;
            }
            plotData(csvData);

            List<Double> results = weeklyForecast(csvData);

            valueOne.setText("$" + String.format("%.2f", results.get(0)));
            valueTwo.setText("$" + String.format("%.2f", results.get(1)));
            valueThree.setText("$" + String.format("%.2f", results.get(2)));
            percentOne.setText(String.format("%.2f", results.get(3)) + "%");
            percentTwo.setText(String.format("%.2f", results.get(4)) + "%");
            percentThree.setText(String.format("%.2f", results.get(5)) + "%");

        }
    }

    private List<Double> weeklyForecast(List<String[]> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            System.out.println("CSV data is empty or null!");
            return null;
        }

        List<Number> weekOne = new ArrayList<>();
        List<Number> weekTwo = new ArrayList<>();
        List<Number> weekThree = new ArrayList<>();


        Double weekOneValueOne = 0.0;
        Double weekTwoValueOne = 0.0;
        Double weekThreeValueOne = 0.0;
        Double weekOnePercentOne = 0.0;
        Double weekOnePercentTwo = 0.0;
        Double weekOnePercentThree = 0.0;

        for (String[] row : csvData) {

            if (row.length < 5) {
                System.out.println("Skipping row (insufficient columns): " + Arrays.toString(row));
                continue;
            }

            try {
                // 2. Parse weekOne (column 3) into double
                double week1 = Double.parseDouble(row[2].trim());

                // 2. Parse weekOne (column 4) into double
                double week2 = Double.parseDouble(row[3].trim());

                // 2. Parse weekOne (column 5) into double
                double week3 = Double.parseDouble(row[4].trim());

                System.out.println(week1 + " " + week2 + " " + week3);

                weekOne.add(week1);
                weekTwo.add(week2);
                weekThree.add(week3);

            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in row: " + Arrays.toString(row));
            }
        }

        weekOneValueOne = weekOne.get(0).doubleValue();
        weekTwoValueOne = weekTwo.get(0).doubleValue();
        weekThreeValueOne = weekThree.get(0).doubleValue();

        weekOnePercentOne = weekOne.get(weekOne.size() - 1).doubleValue();
        weekOnePercentTwo = weekTwo.get(weekTwo.size() - 1).doubleValue();
        weekOnePercentThree = weekThree.get(weekThree.size() - 1).doubleValue();

        List<Double> results = new ArrayList<>();
        results.add(weekOneValueOne);
        results.add(weekTwoValueOne);
        results.add(weekThreeValueOne);
        results.add(weekOnePercentOne);
        results.add(weekOnePercentTwo);
        results.add(weekOnePercentThree);

        if (weekOne.isEmpty() || weekTwo.isEmpty() || weekThree.isEmpty()) {
            System.out.println("No valid data points to fill text!");
        }
        return results;
    }

    private void plotData(List<String[]> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            System.out.println("CSV data is empty or null!");
            return;
        }

        List<Number> xValues = new ArrayList<>(); // Timestamps (long)
        List<Number> yValues = new ArrayList<>(); // Stock prices (double)

        boolean isHeader = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        for (String[] row : csvData) {
            if (isHeader) {
                isHeader = false;
                continue; // Skip header row
            }

            if (row.length < 2) {
                System.out.println("Skipping row (insufficient columns): " + Arrays.toString(row));
                continue;
            }

            try {
                // 1. Parse date (column 0) into timestamp (long)
                Date date = dateFormat.parse(row[0].trim());
                long timestamp = date.getTime(); // Milliseconds since epoch

                // 2. Parse stock price (column 1) into double
                double price = Double.parseDouble(row[1].trim());

                System.out.println("Adding X (timestamp): " + timestamp + ", Y (price): " + price);

                xValues.add(timestamp);
                yValues.add(price);
            } catch (ParseException e) {
                System.err.println("Invalid date format in row: " + Arrays.toString(row));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in row: " + Arrays.toString(row));
            }
        }

        if (xValues.isEmpty() || yValues.isEmpty()) {
            System.out.println("No valid data points to plot!");
            return;
        }

        // Create series (timestamps on X-axis, prices on Y-axis)
        SimpleXYSeries series = new SimpleXYSeries(xValues, yValues, "3 Weeks Forecast");

        // Format line (blue)
        LineAndPointFormatter formatter = new LineAndPointFormatter(
                Color.RED, null, null, null);

        formatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));

        // Add series to plot
        xyPlot.addSeries(series, formatter);

        // Configure X-axis (date) labels
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                .setFormat(new Format() {
                    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd", Locale.US);

                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        long timestamp = ((Number) obj).longValue();
                        return dateFormat.format(new Date(timestamp), toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;
                    }
                });

        // Configure Y-axis (price) labels
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT)
                .setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        double value = ((Number) obj).doubleValue();
                        return toAppendTo.append(String.format(Locale.US, "%.2f", value));
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;
                    }
                });

        // Set label text appearance
        Paint labelPaint = new Paint();
        labelPaint.setColor(Color.BLUE);
        labelPaint.setTextSize(PixelUtils.dpToPix(10));

        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setPaint(labelPaint);
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setPaint(labelPaint);

        // Set axis titles
        xyPlot.setDomainLabel("Date");
        xyPlot.setRangeLabel("Price ($)");

        // Style axis titles
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(PixelUtils.dpToPix(8));
        titlePaint.setFakeBoldText(true);

        xyPlot.getGraph().setLineLabelEdges(XYGraphWidget.Edge.LEFT, XYGraphWidget.Edge.BOTTOM);

        xyPlot.getDomainTitle().getLabelPaint().set(titlePaint);
        xyPlot.getRangeTitle().getLabelPaint().set(titlePaint);

        // Adjust margins to ensure labels fit
        xyPlot.getGraph().setMarginBottom(PixelUtils.dpToPix(10));
        xyPlot.getGraph().setMarginLeft(PixelUtils.dpToPix(10));
        xyPlot.getGraph().setMarginTop(PixelUtils.dpToPix(10));
        xyPlot.getGraph().setMarginRight(PixelUtils.dpToPix(10));

        // Set grid and axis lines to be visible
        xyPlot.getGraph().getDomainGridLinePaint().setColor(Color.LTGRAY);
        xyPlot.getGraph().getRangeGridLinePaint().setColor(Color.LTGRAY);
        xyPlot.getGraph().getDomainOriginLinePaint().setColor(Color.BLACK);
        xyPlot.getGraph().getRangeOriginLinePaint().setColor(Color.BLACK);


        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setRotation(-45);

        // 4. Adjust the step values to ensure labels appear
        xyPlot.setDomainStep(StepMode.SUBDIVIDE, csvData.size() > 10 ? 10 : csvData.size() - 2);
        xyPlot.setRangeStep(StepMode.SUBDIVIDE, 5);

        // 5. Ensure there's enough space for labels
        xyPlot.getGraph().setMarginBottom(PixelUtils.dpToPix(14)); // More space for X labels
        xyPlot.getGraph().setMarginLeft(PixelUtils.dpToPix(15));   // More space for Y labels

        // 6. Make sure the grid lines are visible (helps see where labels should be)
        xyPlot.getGraph().getDomainGridLinePaint().setColor(Color.LTGRAY);
        xyPlot.getGraph().getRangeGridLinePaint().setColor(Color.LTGRAY);

        xyPlot.redraw();
    }
}