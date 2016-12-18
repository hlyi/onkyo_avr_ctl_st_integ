/**
 *  Onkyo Control Device Type for SmartThings
 *  H. Yi
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
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
		command "selDvd"
		command "selCable"
		command "selGame"
		command "selPc"
		command "selAux"
		command "selTv"
		command "selNet"
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
		standardTile("dvd", "device.input", decoration: "flat"){
			state "dvd", label: 'dvd', action: "selDvd", icon:"st.Electronics.electronics5", backgroundColor:"#04eff6"
			state "default", label: 'dvd', action: "selDvd", icon:"st.Electronics.electronics5", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("cable", "device.input", decoration: "flat"){
			state "cable", label: 'cable', action: "selCable", icon:"st.Electronics.electronics3", backgroundColor:"#04eff6"
			state "default", label: 'cable', action: "selCable", icon:"st.Electronics.electronics3", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("game", "device.input", decoration: "flat"){
			state "game", label: 'game', action: "selGame", icon:"st.Electronics.electronics3", backgroundColor:"#04eff6"
			state "default", label: 'game', action: "selGame", icon:"st.Electronics.electronics3", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("pc", "device.input", decoration: "flat"){
			state "pc", label: 'pc', action: "selPc", icon:"st.Electronics.electronics18", backgroundColor:"#04eff6"
			state "default", label: 'pc', action: "selPc", icon:"st.Electronics.electronics18", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("aux", "device.input", decoration: "flat"){
			state "aux", label: 'aux', action: "selAux", icon:"st.Electronics.electronics6", backgroundColor:"#04eff6"
			state "default", label: 'aux', action: "selAux", icon:"st.Electronics.electronics6", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("tv", "device.input", decoration: "flat"){
			state "tv", label: 'tv', action: "selTv", icon:"st.Electronics.electronics6", backgroundColor:"#04eff6"
			state "default", label: 'tv', action: "selTv", icon:"st.Electronics.electronics6", backgroundColor:"#ffffff", defaultState: true
			}
		standardTile("net", "device.input", decoration: "flat"){
			state "net", label: 'net', action: "selNet", icon:"st.Electronics.electronics2", backgroundColor:"#04eff6"
			state "default", label: 'net', action: "selNet", icon:"st.Electronics.electronics18", backgroundColor:"#ffffff", defaultState: true
			}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false, range:"(0..80)") {
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
	details(["switch","mute","dvd","cable","game","pc","aux","tv", "net", "levelSliderControl","zone2"])
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
	sendEvent(name:"switch", value: "on")
}

def off()
{
	sendCommand("PWR00")
	sendEvent(name:"switch", value: "off")
	sendEvent(name:"input", value: "default")
}

def mute()
{
	sendCommand("AMT01")
}

def unmute()
{
	sendCommand("AMT00")
}

def selDvd()
{
//	log.debug("Select DVD")
	sendCommand("SLI10")
	sendEvent(name:"input", value: "dvd" )
}

def selCable()
{
//	log.debug("Select Cable")
	sendCommand("SLI01")
	sendEvent(name:"input", value: "cable" )
}

def selGame()
{
//	log.debug("Select Game")
	sendCommand("SLI02")
	sendEvent(name:"input", value: "game" )
}

def selPc()
{
//	log.debug("Select PC")
	sendCommand("SLI05")
	sendEvent(name:"input", value: "pc" )
}

def selAux()
{
//	log.debug("Select AUX")
	sendCommand("SLI03")
	sendEvent(name:"input", value: "aux" )
}

def selTv()
{
//	log.debug("Select Tv")
	sendCommand("SLI23")
	sendEvent(name:"input", value: "tv" )
}

def selNet()
{
//	log.debug("Select Net")
	sendCommand("SLI2B")
	sendEvent(name:"input", value: "net" )
}

def hubActionCallback(response)
{
//	log.debug(response)
	def status = response?.headers["x-srtb-status"] ?: ""
	if (status != "Ok") {
		log.debug("Reponse error: " + status)
		return
	}
	def retstr = response?.body
	if ( retstr == '' ) return
//	log.debug("Return Str: " + retstr)
	def bytes = retstr.decodeBase64()
	def size = bytes.size()
	def ofst = 0
	while (true){
		if ( (ofst +18 ) > size ) {
			log.debug("Return message too short " + ofst + ", " + size)
			return
		}
		if ( bytes[ofst] != 0x49 || bytes[ofst+1] != 0x53 || bytes[ofst+2] != 0x43 || bytes[ofst+3] != 0x50){
			log.debug("Wrong return signature header: " + bytes[ofst] + bytes[ofst+1] + bytes[ofst+2] + bytes[ofst+3])
			return	
		}
		if ( bytes[ofst+16] != 0x21 || bytes[ofst+17] != 0x31){
			log.debug("Wrong return signature command: " + bytes[ofst+16] + bytes[ofst+17])
			return	
		}
		def int len = ((bytes[ofst+8] & 0xff)<<24) + ( (bytes[ofst+9]&0xff)<<16) + ( (bytes[ofst+10]&0xff)<<8) + (bytes[ofst+11] & 0xff)
		if ( len < 3 ) {
			log.debug ("Data size is too short " + len )
			return
		}
		if ( (ofst + len + 16 ) > size ) {
			log.debug("Return message no enough dat " + ofst + ", " + len + ", " + size)
			return		
		}
		def int j = 0 
//		log.debug("Len = " + len + ", Ofst = " + ofst)
		for (j = ofst +len + 15 ; j > ofst+18; j--) {
			def tmpchar = bytes[j]
			if ( (tmpchar != 0x1a ) && (tmpchar != 0x0d) && (tmpchar != 0x0a)) break
		}
		def msg = new byte[j-ofst-17]
		for ( def i = 0; i < j-ofst-17 ; i++ ) {
			msg[i] = bytes[i+ofst+18]
		}
		def cmdstr = new String(msg)
		log.debug("Recved: " + cmdstr)
		ofst += len + 16
//		log.debug ("new offset" + ofst)
		if ( ofst >= size ) break
	}
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
//	log.debug("Sending: " + command)
	def bridgeIPHex = convertIPtoHex(bridgeIP)
	def bridgePortHex = convertPortToHex(bridgePort)
	def deviceNetworkId = "$bridgeIPHex:$bridgePortHex"
	
	def headers = [:] 
	headers.put("HOST", "$bridgeIP:$bridgePort")	
	headers.put("x-srtb-ip", avrIP)
	headers.put("x-srtb-port", '60128')
	headers.put("x-srtb-timeout", ".1")
	headers.put("x-srtb-repeat", 7)
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
