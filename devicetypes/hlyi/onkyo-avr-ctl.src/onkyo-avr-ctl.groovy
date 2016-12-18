/**
 *  Onkyo Control Device Type for SmartThings
 *  H. Yi
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
 *
 * ISCP commands were found at https://github.com/miracle2k/onkyo-eiscp/blob/master/eiscp-commands.yaml
 */

metadata
{
	definition (name: "onkyo_avr_ctl", namespace: "hlyi", author: "H. Yi") {
		capability "Switch"
		capability "Music Player"
		command "cable"
		command "stb"
		command "pc"
		command "net"
		command "aux"
		command "z2on"
		command "z2off"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
		standardTile("mute", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "unmuted", label:"mute", action:"mute", icon:"st.custom.sonos.unmuted", backgroundColor:"#ffffff", nextState:"muted"
			state "muted", label:"unmute", action:"unmute", icon:"st.custom.sonos.muted", backgroundColor:"#f6ef04", nextState:"unmuted"
			}
		standardTile("cable", "device.switch", decoration: "flat"){
			state "cable", label: 'cable', action: "cable", icon:"st.Electronics.electronics3"
			}
		standardTile("stb", "device.switch", decoration: "flat"){
			state "stb", label: 'shield', action: "stb", icon:"st.Electronics.electronics5"
			}
		standardTile("pc", "device.switch", decoration: "flat"){
			state "pc", label: 'pc', action: "pc", icon:"st.Electronics.electronics18"
			}
		standardTile("net", "device.switch", decoration: "flat"){
			state "net", label: 'net', action: "net", icon:"st.Electronics.electronics2"
			}
		standardTile("aux", "device.switch", decoration: "flat"){
			state "aux", label: 'aux', action: "aux", icon:"st.Electronics.electronics6"
			}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..70)") {
			state "level", label:'${currentValue}', action:"setLevel", backgroundColor:"#ffffff"
			}
		standardTile("zone2", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "off", label:"Enable Zone 2", action:"z2on", icon:"st.custom.sonos.unmuted", backgroundColor:"#ffffff", nextState:"on"
			state "on", label:"Disable Zone 2", action:"z2off", icon:"st.custom.sonos.muted", backgroundColor:"#ffffff", nextState:"off"
			}
		/*   Commenting this out as it doesn't work yet     
		valueTile("currentSong", "device.trackDescription", inactiveLabel: true, height:1, width:3, decoration: "flat") {
			state "default", label:'${currentValue}', backgroundColor:"#ffffff"
			}
		*/
	}

	
	main "switch"
	details(["switch","mute","cable","stb","pc","net","aux","levelSliderControl","zone2"])
}

preferences
{
	input("avrIP", "text", title: "Onkyo AVR IP", required: true, displayDuringSetup: true)
	input("bridgeIP", "text", title: "Bridge IP", required: true, displayDuringSetup: true)
	input("bridgePort", "text", title: "Bridge Port", required: true, displayDuringSetup: true)
}

def parse(desc)
{
	log.debug("Parse Message: " + desc)
}

def on()
{
	sendCommand("PWR01")
    sendCommand("PWRQSTN")
	sendEvent(name:"switch", value: "on")
}

def off()
{
	sendCommand("PWR00")
	sendCommand("PWRQSTN")
	sendEvent(name:"switch", value: "off")
}

def mute()
{
	sendCommand("AMT01")
}

def unmute()
{
	sendCommand("AMT00")
}

def hubActionCallback(response)
{
	//log.debug(response)
	
	def status = response?.headers["x-srtb-status"] ?: ""
	if (status != "Ok") {
		log.debug("Reponse error: " + status)
		return
	}
	def retstr = response?.body
	if ( retstr == '' ) return
	log.debug("Return Str: " + retstr)
    def bytes = retstr.decodeBase64()
    if ( bytes.size() < 21 ) {
    	log.debug("Return message too short " + bytes.size())
        return
	}
    if ( bytes[0] != 0x49 || bytes[1] != 0x53 || bytes[2] != 0x43 || bytes[3] != 0x50){
    	log.debug("Wrong return signature header: " + bytes[0] + bytes[1] + bytes[2] + bytes[3])
		return	
	}
    if ( bytes[16] != 0x21 || bytes[17] != 0x31){
    	log.debug("Wrong return signature command: " + bytes[16] + bytes[17])
		return	
	}
    def int len = bytes[11] - 3
    def cmdbytes = new byte[len]
    for (def i = 0 ; i < len; i++) cmdbytes[i] = bytes[18+i]
    def cmdstr = new String(cmdbytes)
    log.debug("Return CMD: " + cmdstr)
/*
	def jsp = new groovy.json.JsonSlurper().parseText(retstr)
	def state = jsp.system?.get_sysinfo?.relay_state
	if ( state == 1 || state == 0 ){
		status = "on"
		if ( state == 0 ) status = "off"
		sendEvent(name: "switch", value: status, isStateChange: true)
//		log.debug("Send event " + status)
	}
*/
}


private sendCommand(cmd)
{
	sendCommandToAvr(buildCommand(cmd))
}

private buildCommand(cmd)
{
    def eofstr = new byte[2]
	eofstr[0] = 0x0d
    eofstr[1] = 0x0a
	def datlen = cmd.length() + eofstr.size() + 2
	def cmdbuf = new byte[datlen+16]
	def len = 0x10
	def int idx = 0
	cmdbuf[idx++] = 0x49 // 'I'
	cmdbuf[idx++] = 0x53 // 'S'
	cmdbuf[idx++] = 0x43 // 'C'
	cmdbuf[idx++] = 0x50 //	'P'
	cmdbuf[idx++] = len >>24
	cmdbuf[idx++] = (len>>16 ) & 0xff
	cmdbuf[idx++] = (len>>8 ) & 0xff
	cmdbuf[idx++] = len & 0xff
	cmdbuf[idx++] = datlen >>24
	cmdbuf[idx++] = (datlen>>16 ) & 0xff
	cmdbuf[idx++] = (datlen>>8 ) & 0xff
	cmdbuf[idx++] = datlen & 0xff
	cmdbuf[idx++] = 1
	cmdbuf[idx++] = 0
	cmdbuf[idx++] = 0
	cmdbuf[idx++] = 0
	cmdbuf[idx++] = 0x21 //	'!'
	cmdbuf[idx++] = 0x31 //	'1'
	def strbytes = cmd.getBytes()
	for (def i= 0; i < strbytes.size();i++) cmdbuf[idx++] = strbytes[i]
	for (def i= 0; i < eofstr.size(); i++) cmdbuf[idx++] = eofstr[i]
    
//	log.debug ("ECODE: " + cmdbuf.collect{ String.format('%02x', it )}.join(',') )
	return cmdbuf.encodeBase64()
}

private sendCommandToAvr(command)
{
//	def data = command.encodeBase64()
	log.debug("Sending: " + command)
	def bridgeIPHex = convertIPtoHex(bridgeIP)
	def bridgePortHex = convertPortToHex(bridgePort)
	def deviceNetworkId = "$bridgeIPHex:$bridgePortHex"
	
	def headers = [:] 
	headers.put("HOST", "$bridgeIP:$bridgePort")	
	headers.put("x-srtb-ip", avrIP)
	headers.put("x-srtb-port", '60128')
    headers.put("x-srtb-timeout", ".2")
    headers.put("x-srtb-repeat", 5)
	headers.put("x-srtb-data", command)
	try {
		sendHubCommand(new physicalgraph.device.HubAction([
			method: "GET",
			path: "/",
			headers: headers], 
			deviceNetworkId,
			[callback: "hubActionCallback"]
		)) 
	} catch (e) {
		log.debug("Http Error: " + e.message)
	}
}

private String convertIPtoHex(ipAddress) {
	String hex = ipAddress.tokenize('.').collect{ String.format('%02x', it.toInteger() )}.join()
	return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format('%04x', port.toInteger() )
	return hexport
}