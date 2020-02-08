import _ from 'lodash';
import 'plottable/plottable.css';
import * as Plottable from 'plottable';
import DataHandler from "./DataHandler";
// import * as d3 from "d3";

console.log("data=", data);
let dataHandler = new DataHandler(data);

var colorScale = new Plottable.Scales.Color();

// define axes
var xScale = new Plottable.Scales.Linear();
var xAxis = new Plottable.Axes.Numeric(xScale, "bottom");

var yScale = new Plottable.Scales.Category();
var yAxis = new Plottable.Axes.Category(yScale, "left");


var plot = new Plottable.Plots.Rectangle()
    .x(function (d) {
        return d.start;
    }, xScale)
    .x2(function (d) {
        return d.end;
    })
    .y(function (d) {
        return d.name;
    }, yScale)
    .attr("fill", function (d) {
        return d.name;
    }, colorScale)
    .addDataset(new Plottable.Dataset(data));

var chart = new Plottable.Components.Table([
  [yAxis, plot],
  [null, xAxis]
]);

chart.renderTo("#chart-container");


function component() {
    var element = document.createElement('div');

    /* lodash is used here for bundling demonstration purposes */
    element.innerHTML = _.join(['Build', 'together;', 'not', 'alone'], ' ');

    return element;
}

document.body.appendChild(component());
