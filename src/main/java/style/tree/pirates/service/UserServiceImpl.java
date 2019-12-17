package style.tree.pirates.service;

import style.tree.pirates.model.Role;
import style.tree.pirates.model.User;
import style.tree.pirates.repository.RoleRepository;
import style.tree.pirates.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
    	Role userRole = roleRepository.findOne((long)1);
    	HashSet<Role> set = new HashSet<Role>();
    	set.add(userRole);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(set);
        user.setDeaths(0L);
        user.setScore(0D);
        user.setKills(0L);
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Double getKd(Long id)
	{
    	User user = userRepository.findOne(id);    	
    	return calcKd((double)user.getKills(), (double)user.getDeaths());			
	}
    
    @Override
    public Long getRank(User user)
    {
    	long rank = 1L;    	
    	
    	List<User> users = userRepository.findAll(kdSort());
    	
    	for(int i = 0; i < users.size(); i++)
    	{
    		if (users.get(i).getId().equals(user.getId()))
    		{
    				rank = (long) (i + 1);
    				break;
    		}
    	}
    	    	
    	return rank;
    }
    
    @Override
    public List<User> getRanking()
    {
    	return userRepository.findAll(kdSort());
    }
    
    @Override
    public void incrementKills(User user)
    {
    	Long deaths = user.getDeaths();
    	Long kills = user.getKills() + 1L;
    	user.setScore(calcKd((double)kills,(double)deaths));
    	user.setKills(kills);
    	userRepository.save(user);
    }
    
    @Override
    public void incrementDeaths(User user)
    {
    	Long deaths = user.getDeaths() + 1L;
    	Long kills = user.getKills();
    	user.setScore(calcKd((double)kills,(double)deaths));
    	user.setDeaths(deaths);
    	userRepository.save(user);
    }
    
    private Double calcKd(Double k, Double d)
    {
    	if(d.equals(0D))
		{
			return k;
		}    	
    	return k/d;
    }
    
    private Sort kdSort()
    {
    	return new Sort(Sort.Direction.DESC, "score");
    }
}
