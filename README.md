# Timed DCR Choreography with Data

This project includes the implementations in paper **Timed Declarative Choreographies with Data**.

## Define the DCR Choreography in JSON format

There are events and nested groups in DCR choreography. They can be expressed in a formatted JSON file. Time information is inclueded in this JSON file. Examples can be found in the resources directory.

## Model Transformation

Transform a JSON format DCR choreography to a DCR Graph class in Java.

## Projectability and End Up Projection for A role

The implementation follow the ideas in the paper. To get a DCR choreography's end up projections for all the roles:

### 1. Find all the roles in DCR Choreography.

### 2. For each role, determine if it is projectable.

### 3. If so, generate the role's end up projection via following ways:

#### 		1) Get the sigma sets.

#### 		2) Generate the sigma projection.

#### 		3) Generate the end up projection.

## According to the end up projections, generate the end points.

​	Notice that the data part now should be specified in the end points. They are not in the JSON choreography now.

## Examples:

​	Roles use MQTT as a message broker to send messages to simulate the interactions.

​	Examples can be found in the **services** directory. In the main branch, there are 2 senarios and the choreography does not contain time or data, and there are two examples(one is the one in the paper **Declarative Choreographies and Liveness**). In the **jinshayumi-time** branch, the choreography only contains the extension for time. In the **jinshayumi-time-data** branch, the choreography contains both time and data, and the example is the one in the paper **Timed Declarative Choreographies with Data**.

### 

### 
