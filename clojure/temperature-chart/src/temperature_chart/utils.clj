(ns temperature-chart.utils
	(:import (org.jfree.chart ChartFactory ChartUtilities JFreeChart ))
	(:import (org.jfree.data.time TimeSeries Day TimeSeriesCollection ))
	(:import (java.io File)))

(def WIDTH 1024)
(def HEIGHT 768)
(def MAX (Integer/MAX_VALUE))
(def MIN (Integer/MIN_VALUE))

(defn read-file [fileName]
	(with-open [rdr (clojure.java.io/reader fileName)]
		(reduce conj [] (line-seq rdr))))

(defn parse-data [lines]
	(filter #(> (first %) 1300000000)
		(for [line lines]
			(let [res (clojure.string/split line #"\t") timestamp (Long/parseLong (first res))]
				[timestamp
				(Double/parseDouble (second res))
				(Double/parseDouble (nth res 2))]))))

(defn get-date [data]
	(java.util.Date. (* 1000 data)))

(defn get-day [date]
	(.format (java.text.SimpleDateFormat. "dd") date))

(defn generate-chart [data]
	(with-local-vars
		[prevDay -1
		averageT 0
		averageH 0
		maxT MIN
		maxH MIN
		minT MAX
		minH MAX
		counter 0]
		(let [cnt (count data)
			averageTemperature (TimeSeries. "Average Temperature")
			averageHumidity (TimeSeries. "Average Humidity")
			minTemperature (TimeSeries. "Min Temperature")
			minHumidity (TimeSeries. "Min Humidity")
			maxTemperature (TimeSeries. "Max Temperature")
			maxHumidity (TimeSeries. "Max Humidity")
			seriesCollection (TimeSeriesCollection.)]
		(loop [i 0]

			(let [el (nth data i)
				date (get-date (first el))
				day (get-day date)
				humidity (second el)
				temperature (nth el 2)]

			(if (= @prevDay -1)
				(var-set prevDay day))

			(if (not= @prevDay day)
			(do
				(let [tday (Day. date)]
					(.add averageTemperature tday (/ @averageT @counter))
					(.add averageHumidity tday (/ @averageH @counter))
					(.add minTemperature tday @minT)
					(.add minHumidity tday @minH)
					(.add maxTemperature tday @maxT)
					(.add maxHumidity tday @maxH))

				(var-set prevDay day)
				(var-set counter 0)
				(var-set averageT 0)
				(var-set averageH 0)
				(var-set maxT MIN)
				(var-set maxH MIN)
				(var-set minT MAX)
				(var-set minH MAX)))

			(var-set averageT (+ @averageT temperature))
			(var-set averageH (+ @averageH humidity))
			(var-set counter (inc @counter))

			(if (> @minT temperature)
				(var-set minT temperature))
			(if (> @minH humidity)
				(var-set minH humidity))
			(if (< @maxT temperature)
				(var-set maxT temperature))
			(if (< @maxH humidity)
				(var-set maxH humidity))

			(if (< i (dec cnt))
				(recur (inc i)))))

		(.addSeries seriesCollection averageTemperature)
		(.addSeries seriesCollection averageHumidity)
		(.addSeries seriesCollection minTemperature)
		(.addSeries seriesCollection minHumidity)
		(.addSeries seriesCollection maxTemperature)
		(.addSeries seriesCollection maxHumidity)

		(ChartFactory/createTimeSeriesChart "DHT11 Data" "Time" "Value" seriesCollection true true false ))))

(defn save-chart [chart fileName]
	(let [file (File. fileName)]
		(.createNewFile file)
		(ChartUtilities/saveChartAsJPEG file chart WIDTH HEIGHT)))
