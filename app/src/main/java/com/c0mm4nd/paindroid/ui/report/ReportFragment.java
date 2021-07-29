package com.c0mm4nd.paindroid.ui.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.c0mm4nd.paindroid.databinding.FragmentReportBinding;
import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.model.pain.PainRecordViewModel;
import com.c0mm4nd.paindroid.utils.DateUtil;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReportFragment extends Fragment {
    private final String APP_ID = "0ce3bdf69b103284228165374203c3dd";
    private FragmentReportBinding binding;
    private PainRecordViewModel painRecordViewModel;

    private List<Date> lineChartDates = Arrays.asList(DateUtil.getToday(), DateUtil.getToday());
    private List<Boolean> lineChartSwitches = Arrays.asList(false, false, false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);

        // init spinner/selector
        List<String> list = new ArrayList<String>(3);
        list.add("Pain Locations Pie Diagram");
        list.add("Steps Pie Diagram");
        list.add("Pain & Weather Line Chart");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        binding.reportSelectSpinner.setAdapter(spinnerAdapter);
        binding.reportSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        hideAll();
                        binding.painLocPieChart.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        hideAll();
                        binding.stepsPieChart.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        hideAll();
                        binding.painWeatherLineChartLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ignore
            }
        });

        binding.startDateBtn.setText(DateUtil.dateToString(DateUtil.getToday()));
        binding.startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dataPickerDialog = new DatePickerDialog(getContext());
                dataPickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtil.YMDToString(year, month, dayOfMonth);
                        binding.startDateBtn.setText(date);
                        lineChartDates.set(0, DateUtil.stringToDate(date));
                        renderLineChart();
                    }
                });
                dataPickerDialog.show();
            }
        });

        binding.endDateBtn.setText(DateUtil.dateToString(DateUtil.getToday()));
        binding.endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dataPickerDialog = new DatePickerDialog(getContext());
                dataPickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtil.YMDToString(year, month, dayOfMonth);
                        binding.endDateBtn.setText(date);
                        lineChartDates.set(1, DateUtil.stringToDate(date));
                        renderLineChart();
                    }
                });
                dataPickerDialog.show();
            }
        });

        binding.temperatureLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.temperatureLineBtn.setVisibility(View.INVISIBLE);
                binding.temperatureLineBtnClicked.setVisibility(View.VISIBLE);
                lineChartSwitches.set(0, true);
                renderLineChart();
            }
        });
        binding.temperatureLineBtnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.temperatureLineBtnClicked.setVisibility(View.INVISIBLE);
                binding.temperatureLineBtn.setVisibility(View.VISIBLE);
                lineChartSwitches.set(0, false);
                renderLineChart();
            }
        });
        binding.humidLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.humidLineBtn.setVisibility(View.INVISIBLE);
                binding.humidLineBtnClicked.setVisibility(View.VISIBLE);
                lineChartSwitches.set(1, true);
                renderLineChart();
            }
        });
        binding.humidLineBtnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.humidLineBtnClicked.setVisibility(View.INVISIBLE);
                binding.humidLineBtn.setVisibility(View.VISIBLE);
                lineChartSwitches.set(1, false);
                renderLineChart();
            }
        });
        binding.pressureLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.pressureLineBtn.setVisibility(View.INVISIBLE);
                binding.pressureLineBtnClicked.setVisibility(View.VISIBLE);
                lineChartSwitches.set(2, true);
                renderLineChart();
            }
        });
        binding.pressureLineBtnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.pressureLineBtnClicked.setVisibility(View.INVISIBLE);
                binding.pressureLineBtn.setVisibility(View.VISIBLE);
                lineChartSwitches.set(2, false);
                renderLineChart();
            }
        });
        binding.painWeatherLineChartCorrelationTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcCorrelation();
            }
        });


        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);


        painRecordViewModel.getAllLiveList().observe(requireActivity(), new Observer<List<PainRecord>>() {
            @Override
            public void onChanged(List<PainRecord> painRecords) {
                List<PieEntry> painLocList = new ArrayList<>();

                Map<String, Integer> dist = calcDistMap(painRecords);
                dist.forEach(new BiConsumer<String, Integer>() {
                    @Override
                    public void accept(String s, Integer d) {
                        painLocList.add(new PieEntry(d.floatValue(), s));
                    }
                });

                PieDataSet painLocPieDataSet = new PieDataSet(painLocList, "Pain Locations");
                painLocPieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                painLocPieDataSet.setValueTextSize(16f);
                // inspired by https://github.com/PhilJay/MPAndroidChart/issues/4519#issuecomment-544065212
                painLocPieDataSet.setValueFormatter(new PercentFormatter(binding.painLocPieChart));

                PieData painLocPieData = new PieData(painLocPieDataSet);

                binding.painLocPieChart.setData(painLocPieData);
                binding.painLocPieChart.setMinimumHeight(binding.painLocPieChart.getWidth());
                binding.painLocPieChart.setUsePercentValues(true);
                binding.painLocPieChart.setDrawHoleEnabled(false);
                binding.painLocPieChart.getDescription().setEnabled(false);
                binding.painLocPieChart.invalidate();

                Date today = DateUtil.getToday();
                painRecordViewModel.findByDateFuture(today).thenAccept(new Consumer<PainRecord>() {
                    @Override
                    public void accept(PainRecord painRecord) {
                        List<PieEntry> stepsList = new ArrayList<>();
                        stepsList.add(new PieEntry(painRecord.physicalSteps, "Physical Steps"));
                        stepsList.add(new PieEntry(painRecord.goalSteps - painRecord.physicalSteps, "Remaining"));
                        PieDataSet stepsPieDataSet = new PieDataSet(stepsList, "Steps");
                        stepsPieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        stepsPieDataSet.setValueTextSize(16f);
                        PieData stepsPieData = new PieData(stepsPieDataSet);

                        binding.stepsPieChart.setData(stepsPieData);
                        binding.stepsPieChart.setMinimumHeight(binding.stepsPieChart.getWidth());
                        binding.stepsPieChart.setUsePercentValues(false);
                        binding.stepsPieChart.getDescription().setEnabled(false);
                        binding.stepsPieChart.invalidate();
                    }
                });


//                Date start = DateUtil.stringToDate( binding.startDateBtn.getText().toString());
//                Date end = DateUtil.stringToDate( binding.endDateBtn.getText().toString());
            }
        });

        return binding.getRoot();
    }

    private Map<String, Integer> calcDistMap(List<PainRecord> painRecords) {
        Map<String, Integer> rtn = new HashMap<>();
        for (PainRecord painRecord : painRecords) {
            String key = painRecord.painLoc;
            Integer value = rtn.get(key);

            value = value == null ? 1 : value + 1;

            rtn.put(key, value);
        }

        return rtn;
    }

    private void hideAll() {
        binding.painLocPieChart.setVisibility(View.INVISIBLE);
        binding.painLocPieChartErr.setVisibility(View.INVISIBLE);
        binding.stepsPieChart.setVisibility(View.INVISIBLE);
        binding.stepsPieChartErr.setVisibility(View.INVISIBLE);

        binding.painWeatherLineChartLayout.setVisibility(View.INVISIBLE);
    }


    private void renderLineChart() {
        painRecordViewModel.findByDateRangeFuture(lineChartDates.get(0), lineChartDates.get(1)).thenAccept(new Consumer<List<PainRecord>>() {
            @Override
            public void accept(List<PainRecord> painRecords) {
                List<Entry> tempList = new ArrayList<>();
                List<Entry> humidList = new ArrayList<>();
                List<Entry> pressureList = new ArrayList<>();

                for (PainRecord painRecord : painRecords) {
                    tempList.add(new Entry(painRecord.painDate, (float) painRecord.temperature));
                    humidList.add(new Entry(painRecord.painDate, painRecord.humidity));
                    pressureList.add(new Entry(painRecord.painDate, painRecord.pressure));
                }

                List<ILineDataSet> dataSets = new ArrayList<>();
                if (lineChartSwitches.get(0)) {
                    LineDataSet lineTemperature = new LineDataSet(tempList, "Temperature");
                    dataSets.add(lineTemperature);
                }
                if (lineChartSwitches.get(1)) {
                    LineDataSet lineHumid = new LineDataSet(humidList, "Humid");
                    dataSets.add(lineHumid);
                }
                if (lineChartSwitches.get(2)) {
                    LineDataSet linePressure = new LineDataSet(pressureList, "Pressure");
                    dataSets.add(linePressure);
                }

                LineData lineData = new LineData(dataSets);
                lineData.setValueTextSize(16f);

                binding.painWeatherLineChart.setData(lineData);
                binding.painWeatherLineChart.setMinimumHeight(binding.painWeatherLineChart.getWidth());
                binding.painWeatherLineChart.getXAxis().setValueFormatter(new DateXAxisFormatter());
                binding.painWeatherLineChart.invalidate();
                Log.d("DELETEME", "line chart rendered");
            }
        });
    }

    // onclick
    private void calcCorrelation() {
        painRecordViewModel.findByDateRangeFuture(lineChartDates.get(0), lineChartDates.get(1)).thenAccept(new Consumer<List<PainRecord>>() {
            @Override
            public void accept(List<PainRecord> painRecords) {
                if (painRecords.size() == 0) {
                    Toast.makeText(getContext(), "no data", Toast.LENGTH_LONG).show();
                    return;
                }

                List<Double> dateXAxis = new ArrayList<>();
                for (PainRecord painRecord : painRecords) {
                    dateXAxis.add((double) painRecord.painDate);
                }
                dateXAxis = dateXAxis.stream().sorted().collect(Collectors.toList());
                ;
                double[] painLevelAxis = new double[dateXAxis.size()];
                double[] tempAxis = new double[dateXAxis.size()];
                double[] humidAxis = new double[dateXAxis.size()];
                double[] pressureAxis = new double[dateXAxis.size()];

                for (PainRecord painRecord : painRecords) {
                    double date = painRecord.painDate;
                    if (dateXAxis.contains(date)) {
                        int index = dateXAxis.indexOf(date);
                        painLevelAxis[index] = painRecord.painLevel;
                        tempAxis[index] = painRecord.temperature;
                        humidAxis[index] = painRecord.humidity;
                        pressureAxis[index] = painRecord.pressure;
                    }
                }

                double[][] matrix = new double[4][];
                matrix[0] = painLevelAxis;
                matrix[1] = tempAxis;
                matrix[2] = humidAxis;
                matrix[3] = pressureAxis;

                try {
                    double[] prTemp = getPR(painLevelAxis, tempAxis, painLevelAxis.length);
                    double[] prHumid = getPR(painLevelAxis, humidAxis, painLevelAxis.length);
                    double[] prPressure = getPR(painLevelAxis, pressureAxis, painLevelAxis.length);

                    binding.painWeatherLineChartCorrelationTest.setLines(3);
                    binding.painWeatherLineChartCorrelationTest.setText(String.format(Locale.getDefault(),
                            "Temperature: R %.8f P %.8f \n" +
                                    "Humid: R %.8f P %.8f \n" +
                                    "Pressure: R %.8f P %.8f",
                            prTemp[0], prTemp[1],
                            prHumid[0], prHumid[1],
                            prPressure[0], prPressure[1]
                    ));
                } catch (Exception e) {
                    Log.e("DELETEME", e.getLocalizedMessage());
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private double[] getPR(double[] x, double[] y, int length) {
        double[][] matrix = new double[length][2];
        for (int i = 0; i < length; i++) {
            matrix[i][0] = x[i];
            matrix[i][1] = y[i];
        }

        PearsonsCorrelation pc = new PearsonsCorrelation(matrix);

        double r = pc.getCorrelationMatrix().getEntry(0, 1);
        double p = pc.getCorrelationPValues().getEntry(0, 1);

        double[] ret = new double[2];
        ret[0] = r;
        ret[1] = p;

        return ret;
    }
}
