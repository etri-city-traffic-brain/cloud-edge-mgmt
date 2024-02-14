// 메트릭 데이터를 받을 엘리먼트 요소 객체.
const cpu_usage = document.getElementById("cpu_usage");
const mem_usage = document.getElementById("mem_usage");
const disk_usage = document.getElementById("disk_usage");
const cpu_core = document.getElementById("cpu_core");
const mem_total = document.getElementById("mem_total");
const disk_total = document.getElementById("disk_total");

const xhr = {};
xhr.cpu_usage = new XMLHttpRequest();
xhr.mem_usage = new XMLHttpRequest();
xhr.disk_usage = new XMLHttpRequest();
xhr.cpu_core = new XMLHttpRequest();
xhr.mem_total = new XMLHttpRequest();
xhr.disk_total = new XMLHttpRequest();

const url = "http://127.0.0.1:9300/infra/cloudServices/VM1/monitoring";

// 서버로부터 API 기반으로 호출하는 메트릭 데이터 가져오기.
xhr.cpu_usage.open("GET", url + "/cpu_usage", false);
xhr.cpu_usage.send();
xhr.mem_usage.open("GET", url + "/mem_usage", false);
xhr.mem_usage.send();
xhr.disk_usage.open("GET", url + "/disk_usage", false);
xhr.disk_usage.send();
xhr.cpu_core.open("GET", url + "/cpu_core", false);
xhr.cpu_core.send();
xhr.mem_total.open("GET", url + "/mem_total", false);
xhr.mem_total.send();
xhr.disk_total.open("GET", url + "/disk_total", false);
xhr.disk_total.send();

// 원시 메트릭 데이터를 읽기 편한 단위로 변환 및 단위 정보 추가.
const metricData = {};
metricData.cpu_usage = JSON.parse(xhr.cpu_usage.response);
metricData.cpu_usage.forEach((obj) => {
    obj.unit = "%";
    obj.chartColor = "rgba(253, 138, 138, 0.5)";
});
metricData.mem_usage = JSON.parse(xhr.mem_usage.response);
metricData.mem_usage.forEach((obj) => {
    obj.unit = "%";
    obj.chartColor = "rgba(168, 209, 209, 0.5)";
});
metricData.disk_usage = JSON.parse(xhr.disk_usage.response);
metricData.disk_usage.forEach((obj) => {
    obj.unit = "%";
    obj.chartColor = "rgba(158, 161, 212, 0.5)";
});
metricData.cpu_core = JSON.parse(xhr.cpu_core.response);
metricData.cpu_core.forEach((obj) => {
    obj.unit = "Core";
    obj.chartColor = "rgba(253, 138, 138, 0.5)";
});
metricData.mem_total = JSON.parse(xhr.mem_total.response);
metricData.mem_total.forEach((obj) => {
    obj.unit = "GB";
    obj.chartColor = "rgba(168, 209, 209, 0.5)";
    obj.value = obj.value / 1000 / 1000 / 1000;
});
metricData.disk_total = JSON.parse(xhr.disk_total.response);
metricData.disk_total.forEach((obj) => {
    obj.unit = "TB";
    obj.chartColor = "rgba(158, 161, 212, 0.5)";
    obj.value = obj.value / 1000 / 1000 / 1000 / 1000;
});

// 차트 그리는 함수 정의.
function drawChart(element, metricData) {
    // 시간 범위 및 데이터의 개수를 유동적으로 받아 처리하는 부분.
    const timeRange = [];
    const metricValue = [];
    for (let i = 0; i < metricData.length; i++) {
        timeRange.push(metricData[i].now);
        metricValue.push(metricData[i].value);
    }

    // 차트 그리는 부분.
    const chart = new Chart(element, {
        type: "line",
        data: {
            labels: timeRange,
            datasets: [
                {
                    data: metricValue,
                    borderColor: metricData[0].chartColor,
                    backgroundColor: metricData[0].chartColor,
                    fill: true,
                    borderWidth: 1,
                    xAxisID: "x"
                }
            ]
        },
        options: {
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    text: String(element.id)
                }
            },
            responsive: false,
            scales: {
                x: {
                    ticks: {
                        display: false
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: metricData[0].unit
                    },
                    beginAtZero: true
                }
            }
        }
    });
    return chart;
}

function redrawChart(metric, time) {
    const xhr = new XMLHttpRequest();
    const timeRange = [];
    const metricValue = [];

    function refreshData(path, metricData, metricChart) {
        xhr.open("GET", url + path + time, false);
        xhr.send();

        metricData = JSON.parse(xhr.response);
        metricData.forEach((obj) => {
            if (metric == "cpu_usage" || metric == "cpu_core") {
                obj.chartColor = "rgba(253, 138, 138, 0.5)";
            } else if (metric == "mem_usage" || metric == "mem_total") {
                obj.chartColor = "rgba(168, 209, 209, 0.5)";
                if (metric == "mem_total") {
                    obj.value = obj.value / 1000 / 1000 / 1000;
                }
            } else if (metric == "disk_usage" || metric == "disk_total") {
                obj.chartColor = "rgba(158, 161, 212, 0.5)";
                if (metric == "disk_total") {
                    obj.value = obj.value / 1000 / 1000 / 1000 / 1000;
                }
            }
        });

        for (let i = 0; i < metricData.length; i++) {
            timeRange.push(metricData[i].now);
            metricValue.push(metricData[i].value);
        }
        metricChart.data = {
            labels: timeRange,
            datasets: [
                {
                    data: metricValue,
                    borderColor: metricData[0].chartColor,
                    backgroundColor: metricData[0].chartColor,
                    fill: true,
                    borderWidth: 1,
                    fill: true
                }
            ]
        };
        metricChart.update();
    }

    switch (metric) {
        case "cpu_usage":
            refreshData("/cpu_usage?range=", metricData.cpu_usage, cpu_usage_chart);
            break;
        case "mem_usage":
            refreshData("/mem_usage?range=", metricData.mem_usage, mem_usage_chart);
            break;
        case "disk_usage":
            refreshData("/disk_usage?range=", metricData.disk_usage, disk_usage_chart);
            break;
        case "cpu_core":
            refreshData("/cpu_core?range=", metricData.cpu_core, cpu_core_chart);
            break;
        case "mem_total":
            refreshData("/mem_total?range=", metricData.mem_total, mem_total_chart);
            break;
        case "disk_total":
            refreshData("/disk_total?range=", metricData.disk_total, disk_total_chart);
            break;
    }
}

// 최초 차트 렌더링.
const cpu_usage_chart = drawChart(cpu_usage, metricData.cpu_usage);
const mem_usage_chart = drawChart(mem_usage, metricData.mem_usage);
const disk_usage_chart = drawChart(disk_usage, metricData.disk_usage);
const cpu_core_chart = drawChart(cpu_core, metricData.cpu_core);
const mem_total_chart = drawChart(mem_total, metricData.mem_total);
const disk_total_chart = drawChart(disk_total, metricData.disk_total);

function handleTimeButtonClick(buttonId) {
    switch (buttonId) {
        case "cpu-usage-5m":
            redrawChart("cpu_usage", "5m");
            break;
        case "cpu-usage-10m":
            redrawChart("cpu_usage", "10m");
            break;
        case "cpu-usage-30m":
            redrawChart("cpu_usage", "30m");
            break;
        case "mem-usage-5m":
            redrawChart("mem_usage", "5m");
            break;
        case "mem-usage-10m":
            redrawChart("mem_usage", "10m");
            break;
        case "mem-usage-30m":
            redrawChart("mem_usage", "30m");
            break;
        case "disk-usage-5m":
            redrawChart("disk_usage", "5m");
            break;
        case "disk-usage-10m":
            redrawChart("disk_usage", "10m");
            break;
        case "disk-usage-30m":
            redrawChart("disk_usage", "30m");
            break;
        case "cpu-core-5m":
            redrawChart("cpu_core", "5m");
            break;
        case "cpu-core-10m":
            redrawChart("cpu_core", "10m");
            break;
        case "cpu-core-30m":
            redrawChart("cpu_core", "30m");
            break;
        case "mem-total-5m":
            redrawChart("mem_total", "5m");
            break;
        case "mem-total-10m":
            redrawChart("mem_total", "10m");
            break;
        case "mem-total-30m":
            redrawChart("mem_total", "30m");
            break;
        case "disk-total-5m":
            redrawChart("disk_total", "5m");
            break;
        case "disk-total-10m":
            redrawChart("disk_total", "10m");
            break;
        case "disk-total-30m":
            redrawChart("disk_total", "30m");
            break;
    }
}

// 시간 범위 별 이벤트 핸들러.
// [cpu_usage]
document.getElementById("cpu-usage-5m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-usage-5m");
});
document.getElementById("cpu-usage-10m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-usage-10m");
});
document.getElementById("cpu-usage-30m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-usage-30m");
});

// [mem_usage]
document.getElementById("mem-usage-5m").addEventListener("click", () => {
    handleTimeButtonClick("mem-usage-5m");
});
document.getElementById("mem-usage-10m").addEventListener("click", () => {
    handleTimeButtonClick("mem-usage-10m");
});
document.getElementById("mem-usage-30m").addEventListener("click", () => {
    handleTimeButtonClick("mem-usage-30m");
});

// [disk_usage]
document.getElementById("disk-usage-5m").addEventListener("click", () => {
    handleTimeButtonClick("disk-usage-5m");
});
document.getElementById("disk-usage-10m").addEventListener("click", () => {
    handleTimeButtonClick("disk-usage-10m");
});
document.getElementById("disk-usage-30m").addEventListener("click", () => {
    handleTimeButtonClick("disk-usage-30m");
});

// [cpu_core]
document.getElementById("cpu-core-5m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-core-5m");
});
document.getElementById("cpu-core-10m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-core-10m");
});
document.getElementById("cpu-core-30m").addEventListener("click", () => {
    handleTimeButtonClick("cpu-core-30m");
});

// [mem_total]
document.getElementById("mem-total-5m").addEventListener("click", () => {
    handleTimeButtonClick("mem-total-5m");
});
document.getElementById("mem-total-10m").addEventListener("click", () => {
    handleTimeButtonClick("mem-total-10m");
});
document.getElementById("mem-total-30m").addEventListener("click", () => {
    handleTimeButtonClick("mem-total-30m");
});

// [disk_total]
document.getElementById("disk-total-5m").addEventListener("click", () => {
    handleTimeButtonClick("disk-total-5m");
});
document.getElementById("disk-total-10m").addEventListener("click", () => {
    handleTimeButtonClick("disk-total-10m");
});
document.getElementById("disk-total-30m").addEventListener("click", () => {
    handleTimeButtonClick("disk-total-30m");
});
