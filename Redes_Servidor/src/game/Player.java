package game;

public class Player {
	private String name;
	private long lastConnection;
	private boolean inGame;

	public Player(String name, long con){
		this.name=name;
		this.lastConnection=con;
		inGame=false;
	}
	
	public long getLastCon(){
		return lastConnection;
	}
	
	public void setLastCon(long l){
		this.lastConnection=l;
	}
	
	public String getName(){
		return name;
	}
	
	public void setInGame(boolean inGame){
		this.inGame=inGame;
	}
	
	public boolean isInGame(){
		return inGame;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
