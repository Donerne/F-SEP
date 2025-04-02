package com.example.f_sep;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.AsyncTask;

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

public class StockPerformanceActivity extends AppCompatActivity {
    private XYPlot xyPlot;
    private S3Utils s3Utils;

    private static final String accessKey = BuildConfig.ACCESS_KEY;
    private static final String bucketName = BuildConfig.BUCKET_NAME;
    private static final String secretKey = BuildConfig.SECRET_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_performance);

        // Obtaining data from intent
        String company = getIntent().getStringExtra("Selected_Company");
        String ticker = null;
        String key = null;

        if (company != null) {
            // Processing intent data by splitting name and ticker
            String[] parts = company.split(", ");
            String companyName = parts[0];
            ticker = parts[1];
        }

        TextView companyTitle = findViewById(R.id.threeForecastReport);
//        TextView forecastText = findViewById(R.id.forecastText);
        ImageView backButton = findViewById(R.id.backButton);
        Button forecastButton = findViewById(R.id.ForecastButton);
        xyPlot = findViewById(R.id.xyPlot);

        // Define the S3 object key
        key = "raw_prices/" + ticker + "_raw_prices" + ".csv";
        System.out.println(key);

        // Initialize S3Utils once
        s3Utils = new S3Utils(accessKey, secretKey);

        companyTitle.setText(company);
//        forecastText.setText("Stock forecast: " + String.format("%.2f", (Math.random() * 5)) + "% change tomorrow");

        // Fetching and plotting data
        new FetchAndPlotTask().execute(bucketName, key, accessKey, secretKey);

        backButton.setOnClickListener(v -> finish());

        forecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockPerformanceActivity.this, forecast_report_3_weeks.class);
                intent.putExtra("Selected_Company", company);
                startActivity(intent);
            }
        });
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
        }
    }


    private void plotData(List<String[]> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            System.out.println("CSV data is empty or null!");
            return;
        }

        List<Number> xValues = new ArrayList<>(); // Timestamps (long)
        List<Number> yValues = new ArrayList<>(); // Stock prices (double)

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (String[] row : csvData) {

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
        SimpleXYSeries series = new SimpleXYSeries(xValues, yValues, "Last Month Stock Price History");

        // Format line (blue)
        LineAndPointFormatter formatter = new LineAndPointFormatter(
                Color.BLUE, null, null, null);
        // Color.RED,
        formatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));

//        System.out.println("Y-Values: " + yValues);

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