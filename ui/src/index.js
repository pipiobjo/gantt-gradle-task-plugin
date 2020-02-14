import _ from 'lodash';
// import 'plottable/plottable.css';
import './gantt.css';
// import * as Plottable from 'plottable';
import DataHandler from "./DataHandler";
import * as d3 from "d3";

console.log("data=", data);
let dataHandler = new DataHandler(data);


const createDataCacheById = data => {
    return data.reduce((cache, elt) => {
        return Object.assign(cache, {[elt.id]: elt})
    }, {});
}

const createChartSVG = (data, placeholder, {svgWidth, svgHeight, elementHeight, scaleWidth, scaleHeight, fontSize, minStartDate, maxEndDate, margin, showRelations}) => {
    // create container element for the whole chart
    const svg = d3.select(placeholder).append('svg')

        .attr('width', svgWidth)
        .attr('height', svgHeight);

    const xScale =
        d3.scaleLinear()
            .domain([minStartDate, maxEndDate])
            .range([0, scaleWidth]);

    // prepare data for every data element
    const rectangleData = createElementData(data, elementHeight, xScale, fontSize);

    // create data describing connections' lines
    const polylineData = createPolylineData(rectangleData, elementHeight);

    const xAxis = d3.axisBottom(xScale);

    // create container for the data
    const g1 = svg.append('g').attr('transform', `translate(${margin.left},${margin.top})`);

    const linesContainer = g1.append('g').attr('transform', `translate(0,${margin.top})`);
    const barsContainer = g1.append('g').attr('transform', `translate(0,${margin.top})`);

    g1.append('g').call(xAxis);

    // create axes
    const bars = barsContainer
        .selectAll('g')

        .data(rectangleData)
        .enter();


    const singlebar = bars.append('g')
        .attr('id', 'bar-container')
        .attr('transform', d => {
            return `translate(${d.x},${d.y})`
        });

    singlebar
        .append('rect')

        .attr('taskPath', d => {
            'test'
        })
        .attr('width', d => {
            return d.width;
        })
        .attr('height', d => {
            return d.height;
        })
        .style("fill", "orange")
        // .style("stroke", "black")
        // .style("stroke-width", "2")
        .enter()


    singlebar
        .append('text')
        .text(d => d.label)
        .style('fill', '#513eff')
        // .style('fill', 'black')
        .attr('id', d => {
            return d.label
        })
        .style('font-family', 'sans-serif')
        .attr('x', d => {
            return d.labelX;
            // return d.width / 2;
        })
        .attr('y', d => {
            return d.labelY;
            // return d.height / 2;
        })
        .attr('alignment-baseline', 'middle')
        .attr('text-anchor', 'middle')
    singlebar
        .append('title')
        .text(d => d.label);


    // add stuff to the SVG
    // if (showRelations) {
        linesContainer
            .selectAll('polyline')
            .data(polylineData)
            .enter()
            .append('polyline')
            .style('fill', 'none')
            .style('stroke', d => d.color)
            .attr('points', d => d.points);
    // }





};


const createElementData = (data, elementHeight, xScale, fontSize) =>
    data.map((d, i) => {
        const x = xScale(d.startTimeMillis);
        const xEnd = xScale(d.endTimeMillis);
        const y = i * elementHeight * 1.5;
        const width = xEnd - x + 2; // +20
        const height = elementHeight;

        const dependsOn = d.taskDependencies;
        const id = d.taskPath;

        const tooltip = d.taskPath;

        let label = d.taskPath;
        const duration = d.endTimeMillis - d.startTimeMillis

        const labelX = ((width / 2) );
        const labelY = ((height / 2) );

        return {
            x,
            y,
            xEnd,
            width,
            height,
            id,
            dependsOn,
            label,
            labelX,
            labelY,
            tooltip,
            duration
        };
    });


// chart.renderTo("#chart-container");
const createPolylineData = (rectangleData, elementHeight) => {
    // prepare dependencies polyline data
    const cachedData = createDataCacheById(rectangleData);

    // used to calculate offsets between elements later
    const storedConnections = rectangleData.reduce((acc, e) => Object.assign(acc, {[e.id]: 0}), {});

    // create data describing connections' lines
    return rectangleData.flatMap(d =>
        d.dependsOn
            .map(parentId => {
                return cachedData[parentId]
            })
            .map(parent => {
                const color = '#' + (Math.max(0.1, Math.min(0.9, Math.random())) * 0xFFF << 0).toString(16);

                // increase the amount rows occupied by both parent and current element (d)
                storedConnections[parent.id]++;
                storedConnections[d.id]++;

                const deltaParentConnections = storedConnections[parent.id] * (elementHeight / 4);
                const deltaChildConnections = storedConnections[d.id] * (elementHeight / 4);

                const points = [
                    d.x, (d.y + (elementHeight / 2)),
                    d.x - deltaChildConnections, (d.y + (elementHeight / 2)),
                    d.x - deltaChildConnections, (d.y - (elementHeight * 0.25)),
                    parent.xEnd + deltaParentConnections, (d.y - (elementHeight * 0.25)),
                    parent.xEnd + deltaParentConnections, (parent.y + (elementHeight / 2)),
                    parent.xEnd, (parent.y + (elementHeight / 2))
                ];

                return {
                    points: points.join(','),
                    color
                };
            })
    );
};

const createGanttChart = (placeholder, data, {elementHeight, sortMode, showRelations, svgOptions}) => {

    dataHandler.calculateCriticalPath(data);

    // prepare data
    const margin = (svgOptions && svgOptions.margin) || {
        top: elementHeight * 2,
        left: elementHeight * 2
    };

    const scaleWidth = ((svgOptions && svgOptions.width) || 600) ;
    const scaleHeight = Math.max((svgOptions && svgOptions.height) || 200, data.length * elementHeight * 2) - (margin.top * 2);

    const svgWidth = scaleWidth + (margin.left * 2);
    const svgHeight = scaleHeight + (margin.top * 2);

    const fontSize = (svgOptions && svgOptions.fontSize) || 12;

    if (!sortMode) sortMode = 'date';

    if (typeof (showRelations) === 'undefined') showRelations = true;

    // data = parseUserData(data); // transform raw user data to valid values
    // data = sortElements(data, sortMode);


    const {minStartDate, maxEndDate} = dataHandler.findDateBoundaries(data);

    // add some padding to axes
    // minStartDate.subtract(2, 'days');
    // maxEndDate.add(2, 'days');

    createChartSVG(data, placeholder, {
        svgWidth,
        svgHeight,
        scaleWidth,
        elementHeight,
        scaleHeight,
        fontSize,
        minStartDate,
        maxEndDate,
        margin,
        showRelations
    });

};


// start drawing the chart
var chartContainerEle = document.getElementById("chart-container");

let config = {
    "elementHeight": 25,
    "sortMode": null,
    "showRelations": false,
    "svgOptions": {
        "fontSize": 6,
        "width": 1500,
        "height": 1000
    }

}
createGanttChart(chartContainerEle, data, config)
