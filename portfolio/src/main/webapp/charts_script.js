// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const CHINA_PROVINCES_TABLE = [['Zhuang', 16926381], ['Hui', 10586087], ['Man', 10387958],
                               ['Uygur', 10069346], ['Miao', 9426007], ['Yi', 8714393], ['Tujia', 8353912]];

const CHINA_MAP_TABLE = [['Country', 'Population'], ['China', 1400050000]];
const DOM_CONTAINARS_IDS = {
    DEMOGRAPHIC_CHART: 'demographic-chart-container',
    MAP_CHART: 'china-map-container'
}
const CHINA_REGION = 'CN';
const PIE_CHART_TITLE = 'Ethnic Minority Groups (Top 7)';

/* Generate China provinces pie chart */
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawProvincesPieChart);

/* Generate China geo chart */
google.charts.load('current', {
        'packages':['geochart'],
        'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'
      });
google.charts.setOnLoadCallback(drawChinaMap);


/** Creates a pie chart of China provinces and adds it to the page. */
function drawProvincesPieChart() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Ethnic Group');
    data.addColumn('number', 'Count');
        data.addRows(CHINA_PROVINCES_TABLE);

    const options = {
    title: PIE_CHART_TITLE,
    width: 700,
    height: 500
    };

    const chart = new google.visualization.PieChart(document.getElementById(DOM_CONTAINARS_IDS.DEMOGRAPHIC_CHART));
    chart.draw(data, options);
}

/** Creates a geo chart of China and adds it to the page. */
function drawChinaMap() {
    const data = google.visualization.arrayToDataTable(CHINA_MAP_TABLE);

    const options = {
        region: CHINA_REGION,
        width: 500,
        height: 310,
        colorAxis: {colors: ['#4374e0', '#4374e0']}
    };

    const chart = new google.visualization.GeoChart(document.getElementById(DOM_CONTAINARS_IDS.MAP_CHART));
    chart.draw(data, options);
}