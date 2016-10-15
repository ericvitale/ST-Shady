/**
 *  Copyright 2016 ericvitale@gmail.com
 *
 *  Version 1.0.4 - Support for the Window Shade capability.
 *  Version 1.0.3 - Support for auto refresh added when a shade or set of shades moves.
 *  Version 1.0.2 - Supported proper polling.
 *  Version 1.0.1 - Updated the custom button to show the custom level the user selected. Refresh now supported.
 *                    Added align top and align bottom.
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
 *  You can find this device handler @ https://github.com/ericvitale/ST-Shady
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
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
        capability "Window Shade"
        
        command "sceneOne"
        command "sceneTwo"
        command "sceneThree"
        command "sceneFour"
        command "sceneFive"
        command "alignTop"
        command "alignBottom"
        command "stagger"
        
        attribute "sceneOne", "string"
	}
    
    preferences {
    	input "customLevel", "number", title: "Custom Level", required: true, defaultValue: 66, range: "0..100"
        input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
    }

	tiles(scale: 2) {
    
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home9", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home9", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.Home.home9", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.Home.home9", backgroundColor:"#ffffff", nextState:"turningOn"
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
			state "default", label:'${currentValue}%', action:"sceneOne", icon: "st.Weather.weather14"
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
        
        standardTile("top", "device.top", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Align Top', action:"alignTop", icon:"st.Home.home15"
		}
        
        standardTile("bottom", "device.bottom", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Align Bottom', action:"alignBottom", icon:"st.Home.home15"
		}
        

		main(["switch", "level"])
		details(["switchDetails", "ShadeLevel", "levelSliderControl", "sceneOne", "sceneTwo", "sceneThree", "sceneFour", "sceneFive", "refresh", "top", "bottom"])

	}
}

def updated() {
	sendEvent(name: "sceneOne", value: customLevel, display: false , displayed: false)
    log("Custom Level Selected: ${customLevel}.", "INFO")
    log("Debug Level Selected: ${logging}.", "INFO")
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

def alignTop() {
	def level = parent.alignTop()
    if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
}

def alignBottom() {
	def level = parent.alignBottom()
    if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
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
    
	if (level > 0 && leve <= 99) {
		sendEvent(name: "switch", value: "on")
        sendEvent(name: "windowShade", value: "partially open")
	} else if(level == 0) {
		sendEvent(name: "switch", value: "off")
        sendEvent(name: "windowShade", value: "closed")
	} else {
    	sendEvent(name: "windowShade", value: "open")
    }
    
	sendEvent(name: "level", value: level, unit: "%")
}

def setLevel(value, duration) {
	setLevel(value)
}

def open() {
	setLevel(100)
}

def close() {
	setLevel(0)
}

def presetPosition() {
	setLevel(customLevel)
}

def poll() {
	log("polling...", "DEBUG")
    getChildShadeLevels()
}

def refresh() {
	log("refreshing...", "DEBUG")
    getChildShadeLevels()
}

def getChildShadeLevels() {
	
    def level = parent.checkShades()
    
    if (level > 0 && leve <= 99) {
		sendEvent(name: "switch", value: "on")
        sendEvent(name: "windowShade", value: "partially open")
	} else if(level == 0) {
		sendEvent(name: "switch", value: "off")
        sendEvent(name: "windowShade", value: "closed")
	} else {
    	sendEvent(name: "windowShade", value: "open")
    }
	sendEvent(name: "level", value: level, unit: "%")
}

def stagger() {
	parent.stagger()
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: level, unit: "%")
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

def dhVersion() { return "1.0.3" }

/************ End Logging Methods *********************************************************/