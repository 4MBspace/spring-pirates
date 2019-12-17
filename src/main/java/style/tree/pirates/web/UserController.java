package style.tree.pirates.web;

import style.tree.pirates.gameserver.*;

//import style.tree.pirates.gameserver.gameclient.*;
import style.tree.pirates.model.User;
import style.tree.pirates.service.SecurityService;
import style.tree.pirates.service.UserService;
import style.tree.pirates.validator.UserValidator;

import java.io.IOException;
import java.security.Principal;
//import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.codehaus.jackson.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.jboss.jandex.Main;

@Controller
public class UserController {   
	
	@Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model, Principal principal) {
    	
    	if (principal != null)
    	{
			User user = userService.findByUsername(principal.getName());
									
			model.addAttribute("rank", userService.getRank(user));
			model.addAttribute("kills", user.getKills());
			model.addAttribute("deaths", user.getDeaths());
			model.addAttribute("kd", userService.getKd(user.getId()));
    	}
    	
        return "welcome";
    }
    
    //Anon play
    @RequestMapping(value = {"/play"}, method = RequestMethod.GET)
    public String play(Model model) {
        return "play";
    }
    
    //Logged in user play
    @RequestMapping(value = {"/play"}, method = RequestMethod.POST)
    public String playUser(Model model) {
        return "play";
    }
    
    @RequestMapping(value = {"/admin/createserver"}, method = RequestMethod.POST)
    public String createServer(Model model) {
    	
    	//Temp rule: only 1 concurrent gameserver available...
    	if(!GameServer.getGameServers().isEmpty())
    	{
    		return "redirect:/admin/serverstats";
    	}
    	else return "createserver";
    }
    
    @RequestMapping(value = {"/admin/startserver"}, method = RequestMethod.POST)
    public String startServer(	@RequestParam("mapGrid") String mapJson, 
    							@RequestParam("mapName") String mapName, 
    							@RequestParam("mapSize") int mapSize,
    							@RequestParam("treeSpawn") int treeSpawn,
    							@RequestParam("bootySpawn") int bootySpawn,
    							@RequestParam("crateSpawn") int crateSpawn,
    							@RequestParam("bottleSpawn") int bottleSpawn,
    							Model model ) throws JsonMappingException, JsonParseException, IOException
    {
    	System.out.println("importing---------------------------------------------map");
    	String[][] mapGrid = new ObjectMapper().readValue(mapJson, String[][].class);
    	for(String [] inner : mapGrid)
    	{
    		for(String e : inner)
    		{
    			System.out.print(e);
    		}
    		System.out.println();
    	}        	
    	System.out.println("    \""+mapName+"\"");
    	System.out.println();
    	System.out.println("starting---------------------------------------------gameServer");
    	GameServer gameServer = new GameServer();
    	gameServer.setMap(mapGrid);
    	gameServer.setName(mapName);
    	gameServer.setSpawn(treeSpawn, bootySpawn, crateSpawn, bottleSpawn);
    	System.out.println("Server created at port: " + gameServer.startServer().getPort());    
    	
    	//gameServer objects are supposed to only be referenced in GameServer.getGameServers() static arraylist
    	gameServer = null;
    	
    	return "redirect:/admin/serverstats";
    }
    
    @RequestMapping(value = {"/admin/serverstats"}, method = RequestMethod.POST)
    public String serverStatsForm(Model model) {
    	
    	if(GameServer.getGameServers()!= null && !model.containsAttribute("gameServers"))
    	{
    		model.addAttribute("gameServers", GameServer.getGameServers());
    	}
    	else System.out.println("--> gameServer is null!");
        return "serverstats";
    }
    
    @RequestMapping(value = {"/admin/serverstats"}, method = RequestMethod.GET)
    public String serverStats(Model model) {
    	
    	if(GameServer.getGameServers()!= null && !model.containsAttribute("gameServers"))
    	{
    		model.addAttribute("gameServers", GameServer.getGameServers());
    	}
        return "serverstats";
    }
    
    @RequestMapping(value = {"/admin/removeServer"}, method = RequestMethod.POST)
    public String removeServer(@RequestParam(value = "serverPort") Integer port, Model model) {
    	
    	for(GameServer gs : GameServer.getGameServers())
    	{
    		if(gs.getServerState().getPort().equals(port))
    		{
    			gs.closeServer();
    			break;
    		}
    	}    	    	
    	
    	return "redirect:/admin/serverstats";
    }
    
    @RequestMapping(value = {"/admin/kick"}, method = RequestMethod.POST)
    public String kickAll(@RequestParam(value = "serverPort") Integer port, Model model) {
    	    	
    	for(GameServer gs : GameServer.getGameServers())
    	{
    		if(gs.getServerState().getPort().equals(port))
    		{
    			gs.kick();
    			gs.updatePlayers();
    			break;
    		}
    	}    	    	
    	
    	return "redirect:/admin/serverstats";
    }
    
    @RequestMapping(value = "/api/serverlist", method = RequestMethod.GET)
    public @ResponseBody
    String getServerList() throws JsonMappingException, IOException
    {
 
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonInString = "{}"; 
    	
    	List<ServerState> serverStates = new ArrayList<ServerState>();
    	
    	for(GameServer gs : GameServer.getGameServers())
    	{   		
    		serverStates.add(gs.getServerState());
    	}
    	if (!serverStates.isEmpty())
    		jsonInString = mapper.writeValueAsString(serverStates);
    	
       return jsonInString;
    } 
    
    @RequestMapping(value = "/api/kd/{id}", method = RequestMethod.GET)
    public @ResponseBody
    String getKd(@PathVariable("id")Long id, Model model) 
    {
    	return userService.getKd(id).toString();    	
    }
    
    @RequestMapping(value = {"/play/websocket"}, method = RequestMethod.GET)
    public String webSocketTest(Model model) {
    	
        return "websockettest";
    }
    
    @RequestMapping(value = {"/ranking/global"}, method = RequestMethod.POST)
    public String rankingGlobal(Model model) {
    										
		model.addAttribute("userRanking", userService.getRanking());    	
    	
    	return "globalranking";
    }
    
    @RequestMapping(value = "/api/ikills", method = RequestMethod.GET)
    public @ResponseBody
    void incrementKills(Model model, Principal principal) 
    {
    	if(principal != null && principal.getName() != null)
    	{
	    	System.out.print(principal.getName() + " KD: ");
	    	User user = userService.findByUsername(principal.getName());
	    	userService.incrementKills(user);
	    	System.out.println(user.getScore());
    	}
    }
    
    @RequestMapping(value = "/api/ideaths", method = RequestMethod.GET)
    public @ResponseBody
    void incrementDeaths(Model model, Principal principal) 
    {
    	if(principal != null && principal.getName() != null)
    	{
	    	System.out.print(principal.getName() + " KD: ");
	    	User user = userService.findByUsername(principal.getName());
	    	userService.incrementDeaths(user);    
	    	System.out.println(user.getScore());
    	}
    }       
}
