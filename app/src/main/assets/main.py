from youtube_search import YoutubeSearch
from bs4 import BeautifulSoup
import requests
import random

def get_random_video_id():
    while True:
        if random.choice([True, False]):
            word = "IMG " + str(random.randrange(1000, 10000))
        else:
            try:
                r = requests.get("https://en.wikipedia.org/wiki/Main_Page")
                soup = BeautifulSoup(r.content,"html.parser")
                word = random.choice(str(soup.text).split())
            except:
                word = "IMG " + str(random.randrange(1000, 10000))

        results = YoutubeSearch(word, max_results=100).to_dict()

        video_ids = []
        for i in results:
            video_ids.append(i["id"])

        id = None
        try:
            id = random.choice(video_ids)
            break
        except IndexError:
            continue

    return id
