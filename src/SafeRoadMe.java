import java.util.ArrayList;
import org.iiitb.es103_15.traffic.*;

public class SafeRoadMe extends Road {
	int direction;

	public SafeRoadMe(int dir, Intersection start, Intersection end, boolean entryStart, boolean entryEnd) {
		super(dir, start, end, entryStart, entryEnd);
		direction = dir;
	}

	public SafeRoadMe(int dir, Intersection start, Intersection end) {
		this(dir, start, end, true, true);
	}

	public void checkCollisions() {
		Car tempCar;
		ArrayList cars = this.getCarsL(direction);
		if(cars.size()!=0) {
			Car carOne = (Car)cars.get(0);
			carOne.carInFront(null);
			for(int i=1; i< cars.size(); i++){
				tempCar = (Car)cars.get(i);
				tempCar.carInFront(carOne);
				carOne = tempCar;
			}
		}
		ArrayList cars2 = this.getCarsL(RoadGrid.getOppDir(direction));
		if(cars2.size()!=0) {
			Car carOne = (Car)cars2.get(0);
			carOne.carInFront(null);
			for(int i=1; i< cars2.size(); i++){
				tempCar = (Car)cars2.get(i);
				tempCar.carInFront(carOne);
				carOne = tempCar;
			}
		}
	}
}
