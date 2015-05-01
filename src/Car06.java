import java.util.Random;
import org.iiitb.es103_15.traffic.*;

public class Car06 extends Car implements TrafficSignal.SignalListener  {

	Random rand = new Random();
	boolean redLight = false;
	TrafficControl tc = null;
	Car carAhead = null;

	@Override
	public String toString() {
		return ("A06");
	}

	@Override 
	public void setInitialPos(Road r, Coords loc, int dir) {
		super.setInitialPos(r, loc, dir);
	}

	public void carInFront( Car obstacle) {
		this.carAhead=obstacle;
	}

	private int getDist(Car c) {
		Coords c1 = c.getPos();
		Coords c2 = this.getPos();
		int distance = Math.abs((c1.x-c2.x) + (c1.y-c2.y));
		return distance-c.getLength();
	}

	private int getDistIntersection(Intersection i){

		Coords c1 = i.getCoords();
		Coords c2 = this.getPos();
		int distance = Math.abs((c1.x-c2.x) + (c1.y-c2.y));
		return distance;

	}

	@Override
	public void updatePos() {
		super.updatePos();
		int limit = this.getRoad().getSpeedLimit();
		if(limit==10)
			limit=11;
		this.updateRed();
		if(this.carAhead!=null && this.getDist(this.carAhead)<30){
			this.accelerate((this.carAhead.getSpeed()-this.getSpeed())*10);
		}
		else {
			if (this.getDistIntersection(this.getNextIntersection()) < 30){
				this.accelerate((10-this.getSpeed())*10);
				if(this.redLight){
					this.accelerate((0-this.getSpeed())*10);
				}
			}
			else if(limit>this.getSpeed()){
				this.accelerate((this.getRoad().getSpeedLimit()-this.getSpeed())*5);
			}
			else if (limit<this.getSpeed()){
				this.accelerate((this.getRoad().getSpeedLimit()-this.getSpeed() )* 10);
			}
			if(getDistIntersection(this.getNextIntersection())<5) {
				try {
					this.turnDirection();
					this.updateRed();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Intersection getNextIntersection() {
		Road r1 = this.getRoad();
		if(this.getDir()==r1.getDir())
			return r1.getEndIntersection();
		else
			return r1.getStartIntersection();
	}

	public void updateRed () {
		TrafficControl ta = this.getNextIntersection().getTrafficControl();
		if(tc==null) {
			tc=ta;
			if (tc != null && tc.getType() == 0) {
				synchronized (tc) {
					((TrafficSignal)tc).addListener((TrafficSignal.SignalListener)this, this.goOpposite());
					int a =((TrafficSignal)tc).getSignalState(this.goOpposite());
					if(a==1)
						this.redLight=false;
					else
						this.redLight=true;
				}
			}
		}
	}

	public int turnLeft(){
		if(this.getDir()==0)
			return 3;
		else if (this.getDir()==1)
			return 0;
		else if (this.getDir()==2)
			return 1;
		else
			return 2;
	}

	public int turnRight(){ 
		if(this.getDir()==0)
			return 1;
		else if (this.getDir()==1)
			return 2;
		else if (this.getDir()==2)
			return 3;
		else
			return 0;
	}

	public int goStraight() {
		if(this.getDir()==0)
			return 0;
		else if (this.getDir()==1)
			return 1;
		else if (this.getDir()==2)
			return 2;
		else
			return 3;

	}

	public int goOpposite() { 
		if(this.getDir()==0)
			return 2;
		else if (this.getDir()==1)
			return 3;
		else if (this.getDir()==2)
			return 0;
		else
			return 1;
	}


	public void turnDirection() {
		TrafficControl ta = this.getNextIntersection().getTrafficControl();
		Intersection i1 = getNextIntersection();
		Road[] rs = i1.getRoads();
		if (!(this.redLight )) {
			if (ta!=null) {
				synchronized (ta) {
					((TrafficSignal)ta).removeListener((TrafficSignal.SignalListener)this, this.goOpposite());
					tc=null;
					this.redLight=false;
				}
			}
			Intersection trafficInter = this.getNextIntersection();
			synchronized (trafficInter){
				int ps = this.rand.nextInt(4);
				int[]possibilities= new int [4];
				possibilities[0]=turnRight();
				possibilities[1]=turnLeft();
				possibilities[2]=goStraight();
				possibilities[3]=goOpposite();
				while (rs[possibilities[ps]] == null) { 
					ps = this.rand.nextInt(4);
				}
				this.crossIntersection(i1, possibilities[ps]);
			}
		}
	}

	@Override
	public void onChanged(int lightState) {
		if(lightState == 0)
			this.redLight=true;
		else
			this.redLight=false;
	}
}
