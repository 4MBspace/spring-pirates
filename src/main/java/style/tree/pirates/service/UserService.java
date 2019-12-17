package style.tree.pirates.service;

import java.util.List;

import style.tree.pirates.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

	Double getKd(Long id);

	List<User> getRanking();

	void incrementKills(User user);

	void incrementDeaths(User user);

	Long getRank(User user);
}
