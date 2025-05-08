import requests
import time
import json

BASE_URL = "http://localhost:8080/delivery-tasks"
TASK_ID = "6818b4d86f1a9e7d3336ea48"
POLLING_INTERVAL = 0.01
DESTINATION_LAT = 130.29999999999688
DESTINATION_LON = -130.29999999999688
DISTANCE_THRESHOLD = 0.1

def set_task_status():
    url = f"{BASE_URL}/{TASK_ID}"
    headers = {"Content-Type": "application/json"}
    data = "OUT_FOR_DELIVERY"
    try:
        response = requests.patch(url, json=data, headers=headers)
        response.raise_for_status()
        print(f"Set status to OUT_FOR_DELIVERY: {response.json()}")
    except requests.RequestException as e:
        print(f"Error setting status: {e}")

def update_location(current_lat, current_lon):
    url = f"{BASE_URL}/{TASK_ID}/location"
    headers = {"Content-Type": "application/json"}
    data = {"latitude": current_lat, "longitude": current_lon}
    try:
        response = requests.patch(url, json=data, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        print(f"Error updating location: {e}")
        return None

def is_close_to_destination(lat, lon):
    lat_diff = abs(lat - DESTINATION_LAT)
    lon_diff = abs(lon - DESTINATION_LON)
    return lat_diff < DISTANCE_THRESHOLD and lon_diff < DISTANCE_THRESHOLD

def main():
    current_lat = 0
    current_lon =  0

    set_task_status()

    # start polling
    while True:
        current_lat += 0.1
        current_lon -= 0.1

        response = update_location(current_lat, current_lon)
        if response:
            status = response.get("status")
            print(f"Updated location: Lat={current_lat}, Lon={current_lon}, Status={status}")

            # Stop polling if status is DELIVERED or close to destination
            if status == "DELIVERED" or is_close_to_destination(current_lat, current_lon):
                print("Stopping polling: Destination reached or task delivered")
                break

        time.sleep(POLLING_INTERVAL)

if __name__ == "__main__":
    main()