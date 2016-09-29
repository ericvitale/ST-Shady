/**
 *  Copyright 2016 ericvitale@gmail.com
 *
 *  Version 1.0.0 - Initial Release
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Shady Group", namespace: "ericvitale", author: "ericvitale@gmail.com") {
		capability "Switch Level"
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        
        command "sceneOne"
        command "sceneTwo"
        command "sceneThree"
        command "sceneFour"
        command "sceneFive"
	}
    
    preferences {
    	input "customLevel", "number", title: "Custom Level", required: true, defaultValue: 66, range: "0..100"
        input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
    }

	tiles(scale: 2) {
    
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home9.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home9.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.Home.home9.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.Home.home9.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}
		multiAttributeTile(name:"switchDetails", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home9", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home9", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.Home.home9", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.Home.home9", backgroundColor:"#ffffff", nextState:"turningOn"
			}
		}
        
        valueTile("ShadeLevel", "device.level", width: 2, height: 1) {
        	state "level", label: 'Shade is ${currentValue}% up'
        }
        
        controlTile("levelSliderControl", "device.level", "slider", width: 4, height: 1) {
        	state "level", action:"switch level.setLevel"
        }
        
        standardTile("sceneOne", "device.sceneOne", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Custom", action:"sceneOne", icon: "st.Weather.weather14"
		}
        
        standardTile("sceneTwo", "device.sceneTwo", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"20%", action:"sceneTwo", icon: "st.Weather.weather14"
		}
        
        standardTile("sceneThree", "device.sceneThree", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"40%", action:"sceneThree", icon: "st.Weather.weather14"
		}
        
        standardTile("sceneFour", "device.sceneFour", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"60%", action:"sceneFour", icon: "st.Weather.weather14"
		}
        
        standardTile("sceneFive", "device.sceneFive", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"80%", action:"sceneFive", icon: "st.Weather.weather14"
		}

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}

		main(["switch"])
		details(["switchDetails", "ShadeLevel", "levelSliderControl", "sceneOne", "sceneTwo", "sceneThree", "sceneFour", "sceneFive", "refresh"])

	}
}

def sceneOne() {
    setLevel(customLevel)
}

def sceneTwo() {
    setLevel(20)
}

def sceneThree() {
    setLevel(40)
}

def sceneFour() {
    setLevel(60)
}

def sceneFive() {
    setLevel(80)
}

def parse(String description) {
    log("Parse: ${description}.", "INFO")
}

def on() {
	log("Sending command to parent: up().", "DEBUG")
	parent.up()
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "onPercentage", value: 100, displayed: false)
}

def off() {
	log("Sending command to parent: down().", "DEBUG")
	parent.down()
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "onPercentage", value: 0, displayed: false)
}

def setLevel(value) {
	
    def level = value
    
    log("Sending command to parent: setLevel(${level}).", "DEBUG")
    parent.setLevel(level)
    
	if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
}

def setLevel(value, duration) {
	setLevel(value)
}

def poll() {
}

def refresh() {
	log.debug "refresh() is called"
}

def invertSwitch(invert=true) {

}

/************ Begin Logging Methods *******************************************************/

def determineLogLevel(data) {
    switch (data?.toUpperCase()) {
        case "TRACE":
            return 0
            break
        case "DEBUG":
            return 1
            break
        case "INFO":
            return 2
            break
        case "WARN":
            return 3
            break
        case "ERROR":
        	return 4
            break
        default:
            return 1
    }
}

def log(data, type) {
    data = "ShadyGroup -- v${dhVersion()} --  ${data ?: ''}"
        
    if (determineLogLevel(type) >= determineLogLevel(settings?.logging ?: "INFO")) {
        switch (type?.toUpperCase()) {
            case "TRACE":
                log.trace "${data}"
                break
            case "DEBUG":
                log.debug "${data}"
                break
            case "INFO":
                log.info "${data}"
                break
            case "WARN":
                log.warn "${data}"
                break
            case "ERROR":
                log.error "${data}"
                break
            default:
                log.error "ShadyGroup -- Invalid Log Setting"
        }
    }
}

def dhVersion() { return "1.0.0" }

/************ End Logging Methods *********************************************************/