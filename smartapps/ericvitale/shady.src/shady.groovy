/**
 *  Shady
 *
 *  Copyright 2016 ericvitale@gmail.com
 *
 *  Version 1.0.2 - Support for auto refresh added when a shade or set of shades moves.
 *  Version 1.0.1 - Refresh now supported. Added align top and align bottom.
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
 *
 *  You can find this smart app @ https://github.com/ericvitale/ST-Shady
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
 *
 */
 
definition(
    name: "${appName()}",
    namespace: "ericvitale",
    author: "Eric Vitale",
    description: "Create groups of shades and control as a single device.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/ev-public/st-images/shady-1x.png",
    iconX2Url: "https://s3.amazonaws.com/ev-public/st-images/shady-2x.png",
    iconX3Url: "https://s3.amazonaws.com/ev-public/st-images/shady-3x.png")

preferences {
    page(name: "startPage")
    page(name: "parentPage")
    page(name: "childStartPage")
}

private def appName() { return "${parent ? "Shade Group Automation" : "Shady"}" }

def startPage() {
    if (parent) {
        childStartPage()
    } else {
        parentPage()
    }
}

def parentPage() {
	return dynamicPage(name: "parentPage", title: "", nextPage: "", install: true, uninstall: true) {
        section("Create a new child app.") {
            app(name: "childApps", appName: appName(), namespace: "ericvitale", title: "New Shade Automation", multiple: true)
        }
    }
}

def childStartPage() {
	return dynamicPage(name: "childStartPage", title: "", install: true, uninstall: true) {
        
        section("Shades / Blinds") {
             input "selectedShades", "capability.switchLevel", title: "Shades", multiple: true, required: true
             input "groupName", "text", title: "Group Name", required: true
        }
        
		section([mobileOnly:true], "Options") {
	    	label(title: "Assign a name", required: true)
            	input "logging", "enum", title: "Log Level", required: true, defaultValue: "DEBUG", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
    	}
	}
}

def installed() {
    log("Begin installed.", "DEBUG")
    initialization(true)
    log("End installed.", "DEBUG")
}

def updated() {
    log("Begin updated().", "DEBUG")
    initialization(false)
    log("End updated().", "DEBUG")
}

def initialization(installing) {
    log("Begin initialization().", "DEBUG")
    
    if(parent) { 
    	initChild(installing) 
    } else {
    	initParent() 
    }
    
    log("End initialization().", "DEBUG")
}

def initParent() {
	log("Begin initParent()", "DEBUG")
}

def initChild(installing) {
    log("Begin child initialization().", "DEBUG")
    log("shades = ${selectedShades}.", "DEBUG")
    
    def myHubId = ""
    
    if(selectedShades != null) {
    	log("Shades selected...", "INFO")
        selectedShades.each { it->
        	log("Shade: ${it}.", "INFO")
            myHubId = it.hub.id
        }
        log("Shade list complete.", "INFO")
    } else {
    	log("No shades selected.", "INFO")
    }
    
    if(installing) {
    	addChildDevice("ericvitale", "Shady Group", UUID.randomUUID().toString(), myHubId, ["name": groupName, "label": groupName, completedSetup: true])
    }
    
    subscribe(selectedShades, "switch", switchHandler)
    subscribe(selectedShades, "level", switchHandler)
    
    log("End child initialization().", "DEBUG")
}

def switchHandler(evt) {
	log("Shades are moving.", "INFO")
    scheduleRefresh()
}

def up() {
	selectedShades.on()
	log("Recieved up command.", "DEBUG")
}

def down() {
	selectedShades.off()
	log("Recieved down command.", "DEBUG")
}

def setLevel(val) {
	selectedShades.setLevel(val)
	log("Recieved setLevel(${val}) command.", "DEBUG")
}

def alignTop() {
	def top = 0
    
    selectedShades.each { it->
    	log("${it.label} is at ${it.currentValue('level')}%.", "INFO")
        if(it.currentValue('level') > top) {
        	top = it.currentValue('level')
        }
    }
    
    selectedShades.each { it->
    	it.setLevel(top)
    }
    
    return top
}

def alignBottom() {
	def bottom = 100
    
    selectedShades.each { it->
    	log("${it.label} is at ${it.currentValue('level')}%.", "INFO")
        if(it.currentValue('level') < bottom) {
        	bottom = it.currentValue('level')
        }
    }
    
    selectedShades.each { it->
    	it.setLevel(bottom)
    }
    
    return bottom
}

def stagger() {
	def interval = 100 / selectedShades.size()
    def level = 100 + interval
    selectedShades.each { it->
    	it.setLevel(level)
        log("Setting ${it.label} to ${level}%.", "INFO")
        level = level - interval
    }
}

def checkShades() {
	def theLevel = 0
    
	selectedShades.each { it->
    	log("${it.label} is at ${it.currentValue('level')}%.", "INFO")
        if(it.currentValue('level') > 0) {
        	theLevel = it.currentValue('level')
        }
    }
    
    return theLevel
}

def scheduleRefresh() {
	runIn(30, asyncRefresh)
}

def asyncRefresh() {
	log("Running Asynchronous Refresh.", "INFO")
    checkShades()
    getChildDevices().each { it->
    	it.refresh()
    }
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
    data = "Shady -- v${appVersion()} --  ${data ?: ''}"
        
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
                log.error "Shady -- Invalid Log Setting"
        }
    }
}

def appVersion() { return "1.0.2" }

/************ End Logging Methods *********************************************************/