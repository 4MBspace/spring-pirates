package style.tree.pirates.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String passwordConfirm;
    private Set<Role> roles;
    //private String email;
    private Double score;
    private Long kills;
    private Long deaths;
   // @Column(name="score")
   // private int score = 0;
    //@Column(name="kill")
   	//private int kill = 0;
   // @Column(name="death")
   // private int death = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /*
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
*/
	public Double getScore() {
		return score;
	}

	public void setScore() {
		if(deaths.equals(0L))
			this.score = (double)kills;
		
		this.score = (double)kills/(double)deaths;
	}	
	
	public void setScore(Double score) {		
		this.score = score;
	}

	public Long getKills() {
		return kills;
	}
	
	public void setKills(Long kills) {
		this.kills = kills;
	}

	public Long getDeaths() {
		return deaths;
	}
	
	public void setDeaths(Long deaths) {
		this.deaths = deaths;
	}	
}
