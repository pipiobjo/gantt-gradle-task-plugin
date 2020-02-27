import _ from 'lodash';

class DataHandler {

    constructor(data) {
        this.data = data;
    }

    isTaskTupleOnCriticalPath(child, parent, criticalPath){
        let childIdx = criticalPath.findIndex(x => x === child);
        let parentIdx = criticalPath.findIndex(x => x === parent);

        if(childIdx >= 0 && parentIdx >= 0){
            if(childIdx +1 === parentIdx){
                return true
            }
        }
        return false
    }


    calculateCriticalPath() {
        const tasksById =  {}
        data.forEach(entry =>{
            entry.duration = entry.endTimeMillis - entry.startTimeMillis;
            tasksById[entry.taskPath] = entry
        })

        _.mapKeys(tasksById, function(value, key) {
            this.calculateAccumulateDuration(tasksById[key], tasksById)
        }.bind(this));

        // find max accumulateDuration in task list and step through the graph
        const taskList = Object.values(tasksById);
        const criticalPath = [];
        this.buildCriticalPath(taskList, criticalPath, tasksById)

        console.log("detected critical path ", criticalPath)
        return criticalPath

    };

    buildCriticalPath(deps, criticalPath, tasksById) {
        let maxAccumulateDuration = 0;
        let maxidx =-1
        deps.forEach((entry, index, array) => {
            if (maxAccumulateDuration < entry.accumulateDuration) {
                maxAccumulateDuration=entry.accumulateDuration
                maxidx = index
            }
        });
        if (maxidx > -1) {
            const cpEntry = deps[maxidx]
            criticalPath.push(cpEntry.taskPath)
            const newDeps = cpEntry.taskDependencies.map(x => tasksById[x]);
            this.buildCriticalPath(newDeps, criticalPath, tasksById)
        }
    }


    calculateAccumulateDuration(entry, tasksById) {
        let maxDepDuration = 0;
        let maxDepDurationName;
        entry.taskDependencies.forEach(dep => {

            if (!tasksById[dep].accumulateDuration) {
                this.calculateAccumulateDuration(tasksById[dep], tasksById)
            }
            const depDuration = tasksById[dep].accumulateDuration
            if (maxDepDuration < depDuration) {
                maxDepDuration = depDuration;
                maxDepDurationName = dep

            }

        });
        tasksById[entry.taskPath].criticalDurationDep = maxDepDurationName;
        tasksById[entry.taskPath].accumulateDuration = entry.duration + maxDepDuration;
    };

    createDataCacheById (data) {
        return data.reduce((cache, elt) => {
            return Object.assign(cache, {[elt.id]: elt})
        }, {});
    }


    findDateBoundaries() {
        let minStartDate, maxEndDate;

        data.forEach(({ startTimeMillis, endTimeMillis }) => {
            if (!minStartDate || startTimeMillis < minStartDate) minStartDate = startTimeMillis;
            // if (!minStartDate || startDate.isBefore(minStartDate)) minStartDate = moment(startDate);

            // if (!minStartDate || endDate.isBefore(minStartDate)) minStartDate = moment(endDate);

            if (!maxEndDate || endTimeMillis > maxEndDate) maxEndDate = endTimeMillis;

            // if (!maxEndDate || startDate.isAfter(maxEndDate)) maxEndDate = moment(startDate);
        });

        return {
            minStartDate,
            maxEndDate
        };
    };

}
export default DataHandler;