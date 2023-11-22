
from flask import Flask, render_template, url_for, request, redirect, jsonify
import requests
from datetime import datetime
import re
from threading import Thread

app = Flask(__name__)
game_ids = []
game_list=[]
i=0
j=0
def call_api_at_startup():
    global i, game_ids, game_list
    response = requests.get("https://api.steampowered.com/ISteamApps/GetAppList/v0002/?format=json")
    if response.status_code == 200:
        data = response.json()
        games = data.get('applist', {}).get('apps', [])
        
        game_ids = [game['appid'] for game in games]
        # Do something with game_ids, like storing to a database or processing further
        while len(game_list)<100 and len(game_ids)>0:
            game_info = fetch_game_info_steam_api(game_ids[0])
            if game_info is not None:
                game_list.append(game_info)
            game_ids.pop(0)
        
        print("Games fetched successfully! : ", game_list)
    else:
        print("Failed to fetch games from Steam API")
def refill(count):
    global i, game_ids, game_list
    print("Refill started")

    target_length = len(game_list) + count
    while len(game_list) < target_length and game_ids:
        game_info = fetch_game_info_steam_api(game_ids.pop(0))
        if game_info is not None:
            game_list.append(game_info)
        print("Adding game info, current length:", len(game_list))

    print("Games Refilled successfully!: ", len(game_list))

def fetch_game_info_steam_api(app_id):
    url = f"https://store.steampowered.com/api/appdetails?appids={app_id}"
    response = requests.get(url)

    if response.status_code == 200:
        data = response.json()
        game_data = data.get(str(app_id), {}).get("data")

        if not game_data or game_data.get("type") != "game":
            return None

        name = game_data.get("name")
        description = game_data.get("detailed_description")
        clean_desc = re.sub('<.*?>', '', description)
        genres = [genre["description"] for genre in game_data.get("genres", [])]
        genres_string = ', '.join(genres)
        platforms = game_data.get("platforms", {})
        platform_list = [platform for platform in ["windows", "mac", "linux"] if platforms.get(platform)]
        platform_string=', '.join(platform_list)
        price_overview = game_data.get("price_overview", {})
        price = price_overview.get("final_formatted")
        if price == None:
            price="Free"
        header_image = game_data.get("header_image")
        movies = game_data.get("movies", [])
        video_url = movies[0]["mp4"]["480"] if movies else ""

        # Replace this with your way of handling the game data, e.g., creating a Games object
        game_info = {
            "id": app_id,
            "name": name,
            "description": clean_desc,
            "genres": genres_string,
            "platforms": platform_string,
            "price": price,
            "header_image": header_image,
            "video_url": video_url
        }

        return game_info
    else:
        return None

@app.route('/get_games', methods=['GET'])
def get_games():

   
    count = int(request.args.get('count', 10))
    liked_games = list(request.args.get('likedGameIds', []))
    output = []
    k=0
    while k<count:
        if(int(game_list[0]["id"]) not in liked_games):
            output.append(game_list[0])
            k+=1
        game_list.pop(0)
    thread = Thread(target=refill, args=(count+5,))
    thread.start()
    return jsonify({"output": output})
    


if __name__ == "__main__":
    call_api_at_startup()
    app.run(debug=True) 
