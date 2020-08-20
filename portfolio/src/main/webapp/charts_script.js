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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart and adds it to the page. */
function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Ethnic Group');
  data.addColumn('number', 'Count');
        data.addRows([
          ['Zhuang', 16926381],
          ['Hui', 10586087],
          ['Man', 10387958],
          ['Uygur', 10069346],
          ['Miao', 9426007],
          ['Yi', 8714393],
          ['Tujia', 8353912]
        ]);

  const options = {
    'title': 'Ethnic Minority Groups (Top 7)',
    'width': 600,
    'height': 500
  };

  const chart = new google.visualization.PieChart(
      document.getElementById('demographic-chart-container'));
  chart.draw(data, options);
}