import { useEffect, useState } from "react";
import { getConsumptionData } from "../../utils/Utils";
import { Spin } from "antd";
import { DatePicker } from "antd";
import { Chart } from "react-google-charts";
import './MeasurementGraph.css';
import dayjs from 'dayjs';

const MeasurementGraph = (props) =>{
    const [measurementData, setMeasurementData] = useState([]);
    const [loadedData, setLoadedData] = useState(false);
    const [selectedDate, setSelectedDate] = useState(dayjs());
    const [chartData, setChartData] = useState([["Hour", "Consumption (kWh)"]]);
    const userId = props.userId;
    useEffect(()=>{
        async function fetchMeasurementData(){
            try {
                const res = await getConsumptionData(userId);
                console.log(res);
                setMeasurementData(res);
                setLoadedData(true);
            } catch (error) {
                console.error("Failed to fetch user devices:", error);
            }
        }
        fetchMeasurementData();

        
    },[]);

    useEffect(() => {
        if (measurementData.length > 0) {
          const date = selectedDate.format("YYYY-MM-DD");
          const filteredData = filterDataByDate(measurementData, date);
          const hourlyData = calculateHourlyConsumption(filteredData);
          setChartData([["Hour", "Consumption (kWh)"], ...hourlyData]);
        }
      }, [measurementData, selectedDate]);

    const filterDataByDate = (data, selectedDate) => {
        return data.filter(
          (entry) => new Date(entry.timestamp).toISOString().split("T")[0] === selectedDate
        );
      };

    const handleDateChange = (value) => {
        setSelectedDate(value || dayjs());
    }

    const calculateHourlyConsumption = (data) => {
        const groupedData = [];
        for (let i = 0; i < data.length; i += 6) {
          const group = data.slice(i, i + 6); 
          const totalConsumption = group.reduce((sum, reading) => sum + reading.hourlyReading, 0);
          const timestamp = new Date(group[0].timestamp);
          groupedData.push([timestamp, totalConsumption]);
        }
        return groupedData;
      };


    const options = {
        title: "Hourly Energy Consumption",
        hAxis: {
          title: "Time of Day",
          format: "HH:mm",
        },
        vAxis: {
          title: "Consumption (kWh)",
          minValue: 0,
        },
        legend: { position: "bottom" },
        colors: ["#4285F4"],
        pointSize:7
      };


//TODO SELECT THE DEVICE FOR WHICH I SEE MEASUREMENTS

    return (
<div>
    {loadedData ? <div className="chart-container">
        <DatePicker
       onChange={handleDateChange}
        defaultValue={dayjs()}
        format="YYYY-MM-DD"
        style={{ marginBottom: "20px" }}
      />

      <Chart
        chartType="LineChart"
        width="100%"
        height="100%"
        data={chartData}
        options={options}

        formatters={[
            {
              column: 0,
              type: "DateFormat",
              options: {
                timeZone: 0,
              },
            },
          ]}
      />
    </div> : <Spin size="large"/> }
</div>
    );
};

export default MeasurementGraph;