function formatKMBT(y, b, fixed) {
    if(!fixed) fixed = 1;
    var abs_y = Math.abs(y);
    if (abs_y >= 1000000000000)   { return (y / 1000000000000).toFixed(fixed) + "T" }
    else if (abs_y >= 1000000000) { return (y / 1000000000).toFixed(fixed) + "G" }
    else if (abs_y >= 1000000)    { return (y / 1000000).toFixed(fixed) + "M" }
    else if (abs_y >= 1000)       { return (y / 1000).toFixed(fixed) + "K" }
    else if (abs_y < 1 && y > 0)  { return y.toFixed(fixed) }
    else if (abs_y === 0)         { return '0' }
    else                      { return y.toFixed(fixed) }
}

function formatBase1024KMGTP(y, b, fixed) {
    if(!fixed) fixed = 0;
    var abs_y = Math.abs(y);
    if (abs_y >= 1125899906842624)  { return (y / 1125899906842624).toFixed(fixed)+ "P" }
    else if (abs_y >= 1099511627776){ return (y / 1099511627776).toFixed(fixed) + "T" }
    else if (abs_y >= 1073741824)   { return (y / 1073741824).toFixed(fixed) + "G" }
    else if (abs_y >= 1048576)      { return (y / 1048576).toFixed(fixed) + "M" }
    else if (abs_y >= 1024)         { return (y / 1024).toFixed(fixed) + "K" }
    else if (abs_y < 1 && y > 0)    { return y.toFixed(fixed) }
    else if (abs_y === 0)           { return '0' }
    else                        { return y.toFixed(fixed) }
}

var JUIUtils = (function () {
    var
        modules = {},
        juiChart = jui.include("chart.builder"),
        juiTime = jui.include("util.time"),
        chartEl = $(".detail_monitoring"),
        chartList = {
            "serverCpuUsage": "CPU Usage (%)",
            "serverDisk": "Disk read/write (byte/s)",
            "serverNetwork": "Network In/Out (bps)",
            "serverMemoryUsage": "Memory Usage (%)",
            "serverMemory": "Memory (byte/s)",
            "serverCpu": "CPU",
            "serverDiskUsage": "Disk Usage (%)",
            "serverLoad": "Load",
            "serverProcess": "Process",
            "serverSwapUsage": "SWAP (%)"
        },
        chartDisplayKey = {
            "serverCpuUsage":1,
            "serverDisk":2,
            "serverNetwork": 4,
            "serverMemoryUsage": 8,
            "serverMemory": 16,
            "serverCpu": 32,
            "serverDiskUsage": 64,
            "serverLoad": 128,
            "serverProcess": 256,
            "serverSwapUsage": 512
        },
        chartOptions = {
            "serverCpuUsage":{
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 30,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [
                    {
                        type: "area",
                        target: ["CPU"],
                        axis: 0,
                    },
                    {
                        type: "scatter",
                        target: ["CPU"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverCpuUsage");
                    }
                },
                render: true
            },
            "serverDisk":{
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d.Read, d.Write);

                            if (d.Read != 0 || d.Write != 0) {
                                range = [0, Math.pow(2, Math.floor(max).toString(2).length)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatBase1024KMGTP
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Read", "Write"],
                    symbol: "curve",
                    axis: 0,
                },
                    {
                        type: "scatter",
                        target: ["Read", "Write"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatBase1024KMGTP(data[key], null, 0)};

                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverDisk");
                    }
                },
                render: true
            },
            "serverNetwork": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d["Input"], d["Output"]);
                            max = Math.floor(max).toString();

                            if (d.Input != 0 || d.Output != 0) {
                                range = [0, Math.pow(10, max.length - 1) * (parseInt(max.substring(0, 1)) + 1)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatKMBT
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Input", "Output"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Input", "Output"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatKMBT(data[key], null, 2)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverNetwork");
                    }
                },
                render: true
            },
            "serverMemoryUsage": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["MEM"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["MEM"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverMemoryUsage");
                    }
                },
                render: true
            },
            "serverMemory": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = d.buffers + d.cached + d.free + d.shared;

                            if (d.buffers != 0 || d.cached != 0 || d.free != 0 || d.shared != 0) {
                                range = [0, Math.pow(2, Math.floor(max).toString(2).length)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatBase1024KMGTP
                    }
                },
                brush: [{
                    type: "stackarea",
                    target: ["buffers", "cached", "free", "shared"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "stackscatter",
                        target: ["buffers", "cached", "free", "shared"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatBase1024KMGTP(data[key], null, 2)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverMemory");
                    }
                },
                render: true
            },
            "serverCpu": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "stackarea",
                    target: ["intr", "system", "user", "idle"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "stackscatter",
                        target: ["intr", "system", "user", "idle"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverCpu");
                    }
                },
                render: true
            },
            "serverDiskUsage": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Disk"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Disk"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverDiskUsage");
                    }
                },
                render: true
            },
            "serverLoad": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d['1minute'], d['5minute'], d['15minute']);

                            if (d['1minute'] != 0 || d['5minute'] != 0 || d['15minute'] != 0) {
                                range = [0, max];
                            }

                            return range;
                        },
                        step: 4,
                        line: true
                    }
                },
                brush: [{
                    type: "area",
                    target: ["1minute", "5minute", "15minute"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["1minute", "5minute", "15minute"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true,  dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: data[key]};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverLoad");
                    }
                },
                render: true
            },
            "serverProcess": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d['run'], d['total']);

                            if (d['run'] != 0 || d['total'] != 0) {
                                range = [0, max];
                            }

                            return range;
                        },
                        step: 4,
                        line: true
                    }
                },
                brush: [{
                    type: "area",
                    target: ["run", "total"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["run", "total"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(0)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverProcess");
                    }
                },
                render: true
            },
            "serverSwapUsage": {
                width: 448,
                height: 215,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Swap"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Swap"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                event: {
                    "chart.dblclick": function (e) {
                        MonitoringDetailUI.modules.display("serverSwapUsage");
                    }
                },
                render: true
            }
        },
        chartElDetail = $("#detail"),
        chartListDetail = {
            "serverCpuUsage": "CPU Usage (%)",
            "serverDisk": "Disk read/write (byte/s)",
            "serverNetwork": "Network In/Out (bps)",
            "serverMemoryUsage": "Memory Usage (%)",
            "serverMemory": "Memory (byte/s)",
            "serverCpu": "CPU",
            "serverDiskUsage": "Disk Usage (%)",
            "serverLoad": "Load",
            "serverProcess": "Process",
            "serverSwapUsage": "SWAP (%)"
        },
        chartDisplayKeyDetail = {
            "serverCpuUsage":1,
            "serverDisk":2,
            "serverNetwork": 4,
            "serverMemoryUsage": 8,
            "serverMemory": 16,
            "serverCpu": 32,
            "serverDiskUsage": 64,
            "serverLoad": 128,
            "serverProcess": 256,
            "serverSwapUsage": 512
        },
        chartOptionsDetail = {
            "serverCpuUsage":{
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 30,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 100];
                            /*var max = d.buffers + d.cached + d.free + d.shared;*/
                            /*if (d.CPU != 0) {
                                range = [0, Math.pow(2, Math.floor(d.CPU).toString(2).length)];
                            }*/
                            return range;
                        },
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [
                    {
                        type: "area",
                        target: ["CPU"],
                        axis: 0,
                    },
                    {
                        type: "scatter",
                        target: ["CPU"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverDisk":{
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d.Read, d.Write);

                            if (d.Read != 0 || d.Write != 0) {
                                range = [0, Math.pow(2, Math.floor(max).toString(2).length)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatBase1024KMGTP
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Read", "Write"],
                    symbol: "curve",
                    axis: 0,
                },
                    {
                        type: "scatter",
                        target: ["Read", "Write"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatBase1024KMGTP(data[key], null, 0)};

                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverNetwork": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d["Input"], d["Output"]);
                            max = Math.floor(max).toString();

                            if (d.Input != 0 || d.Output != 0) {
                                range = [0, Math.pow(10, max.length - 1) * (parseInt(max.substring(0, 1)) + 1)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatKMBT
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Input", "Output"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Input", "Output"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatKMBT(data[key], null, 2)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverMemoryUsage": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: [0, 100],
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["MEM"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["MEM"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverMemory": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = d.buffers + d.cached + d.free + d.shared;

                            if (d.buffers != 0 || d.cached != 0 || d.free != 0 || d.shared != 0) {
                                range = [0, Math.pow(2, Math.floor(max).toString(2).length)];
                            }

                            return range;
                        },
                        step: 4,
                        line: true,
                        format: formatBase1024KMGTP
                    }
                },
                brush: [{
                    type: "stackarea",
                    target: ["buffers", "cached", "free", "shared"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "stackscatter",
                        target: ["buffers", "cached", "free", "shared"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: formatBase1024KMGTP(data[key], null, 2)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverCpu": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: [0, 100],
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "stackarea",
                    target: ["intr", "system", "user", "idle"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "stackscatter",
                        target: ["intr", "system", "user", "idle"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverDiskUsage": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: [0, 100],
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Disk"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Disk"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverLoad": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d['1minute'], d['5minute'], d['15minute']);

                            if (d['1minute'] != 0 || d['5minute'] != 0 || d['15minute'] != 0) {
                                range = [0, max];
                            }

                            return range;
                        },
                        step: 4,
                        line: true
                    }
                },
                brush: [{
                    type: "area",
                    target: ["1minute", "5minute", "15minute"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["1minute", "5minute", "15minute"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true,  dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: data[key]};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverProcess": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: function (d) {
                            var range = [0, 10];
                            var max = Math.max(d['run'], d['total']);

                            if (d['run'] != 0 || d['total'] != 0) {
                                range = [0, max];
                            }

                            return range;
                        },
                        step: 4,
                        line: true
                    }
                },
                brush: [{
                    type: "area",
                    target: ["run", "total"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["run", "total"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(0)};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            },
            "serverSwapUsage": {
                width: 728,
                height: 348,
                theme: "dark",
                padding : {
                    left: 50,
                    top: 25,
                    right : 35,
                    bottom : 45
                },
                axis: {
                    x: {
                        type: "dateblock",
                        interval: function(){		// 단위 시간에 대한 간격 설정
                            return parseInt($("#detail_hour").val()) * 60 / 5;
                        },
                        realtime: "minutes",		// 단위 시간 설정
                        format: "HH:mm"
                    },
                    y: {
                        type: "range",
                        domain: [0, 100],
                        step: 4,
                        line: true,
                        format: function (value) {
                            return value + "%";
                        }
                    }
                },
                brush: [{
                    type: "area",
                    target: ["Swap"],
                    symbol: "curve",
                    axis: 0
                },
                    {
                        type: "scatter",
                        target: ["Swap"],
                        hide: true
                    }
                ],
                widget: [
                    {
                        type: 'legend', render: false, filter: true, brush: [0, 1], brushSync: true, dx: 15
                    },
                    {
                        type: 'tooltip',
                        brush: 1,
                        format: function (data, key) {
                            return {key: key, value: (data[key]).toFixed(2) + "%"};
                        }
                    },
                    {
                        type: "cross",
                        xFormat: function (d) {
                            return juiTime.format(d, "HH:mm:ss");
                        },
                        axis: 0
                    }
                ],
                style: {
                    titleFontSize: 12,
                    titleFontWeight: 700,
                    legendFontSize: 11
                },
                render: true
            }
        },
        init = function(){
            var monitoring = {
                "juiChart" : juiChart,
                "chartEl" : chartEl,
                "chartList" : chartList,
                "chartOptions" : chartOptions,
                "chartDisplayKey" : chartDisplayKey,
            };
            modules.monitoring = monitoring;

            var monitoringDetail = {
                "juiChart" : juiChart,
                "chartEl" : chartElDetail,
                "chartList" : chartListDetail,
                "chartOptions" : chartOptionsDetail,
                "chartDisplayKey" : chartDisplayKeyDetail,
            };
            modules.monitoringDetail = monitoringDetail
        };

    return {
        init : init,
        modules : modules
    }
})();
JUIUtils.init();

var MonitoringUI = (function(){
    var
        modules={},
        interval = null,
        juiChart = JUIUtils.modules.monitoring.juiChart,
        chartEl = JUIUtils.modules.monitoring.chartEl,
        chartList = JUIUtils.modules.monitoring.chartList,
        chartOptions = JUIUtils.modules.monitoring.chartOptions,
        chartDisplayKey = JUIUtils.modules.monitoring.chartDisplayKey,
        getChartUrl = function () {
            var m = ServerUI.modules.view.currentSelRow();
            if (m) {
                return '/private/openstack/servers/' + m.get('id') + '/metric?id=' + id;
            }
            return "";
        },
        setChartParam = function(){
            var ajaxData = new Object();
            ajaxData.metricName = 1023; // 10 chart types
            ajaxData.statistic = "MEAN";
            ajaxData.hour = $("#hour").val();
            switch (ajaxData.hour) {
                case "1" :
                case "3" :
                case "6" :
                case "12" :
                case "24" :
                case "72" :
                    ajaxData.interval = 300; // 5 minute
                    break;
                case "168":
                case "332":
                    ajaxData.interval = 1800; // 30 minute
                    break;
            }
            ajaxData.endDate = new Date().getTime() - (1000 * 60);
            ajaxData.endDate2 = new Date().getTime() - (1000 * 60);
            var data = chartList[Object.keys(chartList)[0]].axis(0).data;
            if(data.length == 0){
                ajaxData.startDate =  ajaxData.endDate - (1000 * 60 * 60 * ajaxData.hour);
                ajaxData.startDate2 =  ajaxData.endDate2 - (1000 * 60 * 60 * ajaxData.hour);
            }else{
                ajaxData.startDate = ajaxData.endDate - (1000 * 60);
                ajaxData.startDate2 = ajaxData.endDate - (1000 * 60);
            }
            ajaxData.endDate = Math.floor(ajaxData.endDate / 1000)
            ajaxData.startDate  = Math.floor(ajaxData.startDate / 1000)
            return ajaxData;
        },
        stop = function(){
            if (interval) {
                clearInterval(interval);
                interval = null;
            }
            for(var chart in chartList){
                var charData = chartList[chart];
                if (charData != null) {
                    charData.axis(0).set("x", {domain:[0,0]});
                    charData.axis(0).update([]);
                }
            }
        },
        start = function(){
            modules.displayEffect(); // Chart Popup Control

            var ajaxData = setChartParam();
            $.ajax({
                type: 'get'
                , dataType: "json"
                , url: getChartUrl()
                , data: ajaxData
                , beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                }
                , complete: function () {}
                , success: function (serverData) {
                    $.each(Object.keys(chartList), function (index, chart) {
                        var oldData = chartList[chart].axis(0).data;
                        if (!$.isEmptyObject(serverData)) {
                            var newData = [];
                            var targets = chartList[chart].options.brush[0].target;

                            $.each(targets, function(tindex, target){
                                var currentChartData = serverData[chart+target];

                                if(currentChartData !== undefined){
                                    if(targets.length > 1){
                                        for(var i=0 ; i < currentChartData.length; i++) {
                                            var obj = {};
                                            obj[target] = serverData[chart+target][i];
                                            if(newData.length < currentChartData.length) {
                                                newData.push(obj);
                                            }else {
                                                newData[i] = $.extend(newData[i], obj);
                                            }
                                        }
                                    }else {
                                        for(var i=0 ; i< currentChartData.length; i++){
                                            var obj = {};
                                            obj[target] = currentChartData[i];
                                            newData.push(obj);
                                        }
                                    }
                                }
                            });

                            // key length check
                            $.each(newData, function(index, data){
                                if(targets.length != Object.keys(data).length){
                                    newData.splice(index, index+1);
                                }
                            });

                            if (oldData.length > 0) {
                                $.each(newData, function (index, data) {
                                    oldData.shift();
                                });
                            }

                            var datas = oldData.concat(newData);
                            if(datas.length > 0){
                                chartList[chart].axis(0).set("x", {domain: [ajaxData.endDate2 - (1000 * 60 * 60 * ajaxData.hour), ajaxData.endDate2]});
                                chartList[chart].axis(0).update(datas);
                                MonitoringUI.modules.loadingEfftect("off", chart);
                            }else{
                                MonitoringUI.modules.loadingEfftect("nodata",chart);
                            }
                        }else{
                            MonitoringUI.modules.loadingEfftect("nodata");
                        }
                    });

                }
                , error: function (jqXHR, textStatus, errorThrown) {
                    //alert("system error: " + textStatus);
                }
            });
        },
        repeat = function(){
            // interval = setInterval(function () {
            //     start();
            // }, 1000 * 5 * 60);
        },
        reload = function(m){
            stop();
            start();
            repeat();
        },
        display = function(){
            var chartKeys = Object.keys(chartList);
            for(var i=0; i< chartKeys.length; i++){
                var chart = chartKeys[i];
                var chartId = "#" + chart;
                var chartElement = $(chartId);
                if(chartElement.length==0){
                    chartEl.append($(
                        "<div class='detail_monitoring_box'>" +
                        "<div class='detail_monitoring_tit'>"+chartList[chart]+
                        "<button type='button'><span class='ico_window'>zoom</span></button>" +
                        "</div>"+
                        "<div class='detail_monitoring_convas'></div>"+
                        "<div class='detail_monitoring_loading'></div>"+
                        "<div class='detail_monitoring_nodata' sytle='display: none;'></div>"+
                        "</div>"));
                    chartEl.find(".detail_monitoring_convas:eq("+i+")").attr("id", chart);
                    chartList[chart] = juiChart(chartId, chartOptions[chart]);
                }
            }
        },
        displayEffect = function(){
            var serverMetric = ($.cookie("serverMetric") == null) ? 16383 : ($.cookie("serverMetric"));
            for(var chart in chartList){
                if ((serverMetric & chartDisplayKey[chart]) > 0) {
                    $("#" + chart).parent().show();
                } else {
                    $("#" + chart).parent().hide();
                }
            };
        },
        loadingEffect = function(type, chart){
            switch (type) {
                case "on":
                    chartEl.find(".detail_monitoring_loading").show();
                    chartEl.find(".detail_monitoring_nodata").hide();
                    break;
                case "off":
                    chartEl.find("div[id='"+chart+"']").next().hide();
                    chartEl.find("div[id='"+chart+"']").next().next().hide();
                    break;
                case "nodata":
                    chartEl.find("div[id='"+chart+"']").next().hide();
                    chartEl.find("div[id='"+chart+"']").next().next().show();
                    break;
            }
        },
        init = function(){
            modules.stop = stop;
            modules.start = start;
            modules.repeat = repeat;
            modules.reload = reload;
            modules.display = display;
            modules.displayEffect = displayEffect;
            modules.loadingEfftect = loadingEffect;

            modules.display();
        };

    return {
        init : init,
        modules : modules
    };
})(config);

MonitoringUI.init();

var MonitoringDetailUI = (function(){
    var
        modules={},
        interval = null,
        juiChart = JUIUtils.modules.monitoringDetail.juiChart,
        chartEl = JUIUtils.modules.monitoringDetail.chartEl,
        chartList = JUIUtils.modules.monitoringDetail.chartList,
        chartDisplayKey = JUIUtils.modules.monitoringDetail.chartDisplayKey,
        chartNameList = $.extend({}, chartList),
        chartOptions = JUIUtils.modules.monitoringDetail.chartOptions,
        timer,
        isPause=false,
        select_interval=null,
        getChartUrl = function () {
            var m = ServerUI.modules.view.currentSelRow();
            if (m) {
                return '/private/openstack/servers/' + m.get('id') + '/metric?id=' + id;
            }
            return "";
        },
        MonitoringDetailView = Backbone.View.extend({
            el: "#popupMonitoring",
            events: {
                "click .btn_pop_close" : "close",
                "click .pop_btns .btn_action" : "close",
                "change #detail_statistic": "statisticRender",
                "change #detail_hour": "hourRender",
                "change #detail_interval": "intervalRender",
                "click button.btn_control" : "reload",
            },
            intervalTemplate: _.template('<option value="{{= intervalValue}}">{{= intervalKey}}</option>'),
            init: function(chartName, model){
                this.displayRender();
                this.$el.find(".pop_tit").html(chartNameList[chartName]+ "  for  " + model.get("name"));
                this.$el.find("#detail_statistic option:eq(0)").prop("selected", true);
                this.$el.find("#detail_hour option:eq(0)").prop("selected", true);
                this.$el.find("#detail_interval option:eq(0)").prop("selected", true);
                this.$el.find("select").selectric('refresh');
                select_interval=this.$el.find("#detail_interval option:selected").val();
                this.network_log(select_interval);
            },
            statisticRender: function(){
                this.$el.find("#detail_hour option:eq(0)").prop("selected", true);
                this.$el.find("#detail_interval option:eq(0)").prop("selected", true);
                this.$el.find("select").selectric('refresh');
                this.reload();
            },
            hourRender: function(e){
                var self = this;
                switch (parseInt($(e.currentTarget).val())) {
                    case 1: self.displayRender(1); break;
                    case 3: self.displayRender(3); break;
                    case 6: self.displayRender(6); break;
                    case 12: self.displayRender(12); break;
                    case 24: self.displayRender(24); break;
                    case 72: self.displayRender(72); break;
                    case 168: self.displayRender(168); break;
                    case 332: self.displayRender(332); break;
                    default: self.displayRender(); break;
                }
                this.reload();
            },
            intervalRender: function(){
                this.network_log_stop();
                select_interval=this.$el.find("#detail_interval option:selected").val();
                console.log("select_interval: '" + select_interval );
                this.network_log(select_interval);
                this.reload();
            },
            network_log:function(){
                isPause=false;
                select_interval=select_interval*1000;
                console.log("select_interval: '" + select_interval );
                if(!isPause) {
                    timer = setInterval(reload, select_interval);
                }
            },
            network_log_stop:function(){
                clearInterval(timer);
                isPause = true;
            },
            displayRender: function(interval){
                var self = this;
                var intervalList = [];
                switch (parseInt(interval)) {
                    case 1:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"1분", intervalValue:60});
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        break;
                    case 3:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"1분", intervalValue:60});
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        break;
                    case 6:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"1분", intervalValue:60});
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        break;
                    case 12:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"1분", intervalValue:60});
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        intervalList.push({intervalKey:"360분", intervalValue:21600});
                        break;
                    case 24:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        intervalList.push({intervalKey:"360분", intervalValue:21600});
                        intervalList.push({intervalKey:"720분", intervalValue:43200});
                        break;
                    case 72:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        intervalList.push({intervalKey:"360분", intervalValue:21600});
                        intervalList.push({intervalKey:"720분", intervalValue:43200});
                        intervalList.push({intervalKey:"1440분", intervalValue:86400});
                        break;
                    case 168:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        intervalList.push({intervalKey:"360분", intervalValue:21600});
                        intervalList.push({intervalKey:"720분", intervalValue:43200});
                        intervalList.push({intervalKey:"1440분", intervalValue:86400});
                        break;
                    case 332:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        intervalList.push({intervalKey:"60분", intervalValue:3600});
                        intervalList.push({intervalKey:"360분", intervalValue:21600});
                        intervalList.push({intervalKey:"720분", intervalValue:43200});
                        intervalList.push({intervalKey:"1440분", intervalValue:86400});
                        break;
                    default:
                        $("#detail_interval").empty();
                        intervalList.push({intervalKey:"1분", intervalValue:60});
                        intervalList.push({intervalKey:"5분", intervalValue:300});
                        intervalList.push({intervalKey:"15분", intervalValue:900});
                        intervalList.push({intervalKey:"30분", intervalValue:1800});
                        break;
                }
                $.each(intervalList, function(index, interval){
                    self.$el.find("#detail_interval").append(self.intervalTemplate(interval));
                });
                this.$el.find("#detail_interval option:eq(1)").prop("selected", true);
                $("#detail_interval").selectric('refresh');
            },
            reload: function(){
                modules.loadingEfftect("on");
                modules.reload();
            },
            close: function(){
                this.network_log_stop();
                modules.clear();
                this.$el.fadeOut(100);
            },
            render: function(){
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            }
        }),
        display = function(chartName){
            var display = true
            if($("#popupMonitoring").css("display") == "block"){
                display == false;
            }
            var model = ServerUI.modules.view.currentSelRow();
            if (chartOptions[chartName] != undefined && model && display) {
                modules.view.init(chartName, model);
                modules.currentChartName = chartName;
                chartList[chartName] = juiChart(chartEl.selector, chartOptions[chartName]);
                modules.loadingEfftect("on");
                modules.reload();
            }
        },
        stop = function(){
            if (interval) {
                clearInterval(interval);
                interval = null;
            }
            var charData = chartList[modules.currentChartName];
            if (charData != null) {
                charData.axis(0).set("x", {domain: [0, 0]});
                charData.axis(0).update([]);
            }
        },
        start = function (){
            var ajaxData = new Object();
            var chart = modules.currentChartName;
            ajaxData.metricName = chartDisplayKey[chart];
            ajaxData.statistic = $("#detail_statistic").val();
            ajaxData.hour = $("#detail_hour").val();
            ajaxData.interval = $("#detail_interval").val();
            ajaxData.endDate = new Date().getTime() - (1000 * 60);
            ajaxData.endDate2 = new Date().getTime() - (1000 * 60);
            console.log("endDate : " + ajaxData.endDate);
            ajaxData.startDate =  ajaxData.endDate - (1000 * 60 * 60 * ajaxData.hour);
            ajaxData.startDate2 =  ajaxData.endDate2 - (1000 * 60 * 60 * ajaxData.hour);
            ajaxData.endDate = Math.floor(ajaxData.endDate / 1000)
            ajaxData.startDate  = Math.floor(ajaxData.startDate / 1000)

            $.ajax({
                type: 'get'
                , dataType: "json"
                , url: getChartUrl()
                , data: ajaxData
                , beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                }
                , complete: function () {}
                , success: function (serverData) {
                    var oldData = chartList[chart].axis(0).data;
                    if (!$.isEmptyObject(serverData)) {
                        var newData = [];
                        var targets = chartList[chart].options.brush[0].target;
                        $.each(targets, function (tindex, target) {
                            var currentChartData = serverData[chart + target];
                            if (targets.length > 1) {
                                for (var i = 0; i < currentChartData.length; i++) {
                                    var obj = {};
                                    obj[target] = serverData[chart + target][i];
                                    if (newData.length < currentChartData.length) {
                                        newData.push(obj);
                                    } else {
                                        newData[i] = $.extend(newData[i], obj);
                                    }
                                }
                            } else {
                                for (var i = 0; i < currentChartData.length; i++) {
                                    var obj = {};
                                    obj[target] = currentChartData[i];
                                    newData.push(obj);
                                }
                            }
                        });
                        // key length check
                        $.each(newData, function (index, data) {
                            if (targets.length != Object.keys(data).length) {
                                newData.splice(index, index + 1);
                            }
                        });
                        if (oldData.length > 0) {
                            $.each(newData, function (index, data) {
                                oldData.shift();
                            });
                        }
                        var datas = oldData.concat(newData);
                        if(datas){
                            console.log("endDate : " + ajaxData.endDate);
                            chartList[chart].axis(0).set("x", {domain: [ajaxData.endDate2 - (1000 * 60 * 60 * ajaxData.hour), ajaxData.endDate2]});
                            chartList[chart].axis(0).update(datas);
                        }
                        MonitoringDetailUI.modules.loadingEfftect("off");
                    }else{
                        MonitoringDetailUI.modules.loadingEfftect("nodata");
                    }
                }
                , error: function (jqXHR, textStatus, errorThrown) {
                    //alert("system error: " + textStatus);
                }
            });
            MonitoringDetailUI.modules.view.render();
        },
        repeat = function(){
            // interval = setInterval(function () {
            //     start();
            // }, 1000 * 5 * 60);
        },
        clear = function () {
            if (interval) {
                clearInterval(interval);
                interval = null;
            }
            var currentChart = chartList[modules.currentChartName];
            if (currentChart != null) {
                currentChart.destroy();
                var call_list = jui.get("chart.builder");
                // jui.remove(call_list.findIndex(x => x.selector === chartEl.selector));
                jui.remove(_.indexOf(call_list, _.findWhere(call_list, {selector: chartEl.selector})));
                chartEl.empty();
                modules.currentChartName = null;
            }
        },
        reload = function(){
            stop();
            start();
            repeat();
        },
        loadingEffect = function(type){
            switch (type) {
                case "on":
                    chartEl.next().show();        // loading
                    chartEl.next().next().hide(); // nodata
                    break;
                case "off":
                    chartEl.next().hide();        // loading
                    chartEl.next().next().hide(); // nodata
                    break;
                case "nodata":
                    chartEl.next().hide();          // loading
                    chartEl.next().next().show();   // nodata
                    break;
            }
        },
        init = function(){
            modules.stop = stop;
            modules.start = start;
            modules.repeat = repeat;
            modules.reload = reload;
            modules.clear = clear;
            modules.loadingEfftect = loadingEffect;
            modules.display = display;

            modules.view = new MonitoringDetailView();
        };


    return {
        init : init,
        modules : modules
    };
})(config);

MonitoringDetailUI.init();