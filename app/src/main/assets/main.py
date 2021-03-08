from youtube_search import YoutubeSearch
from youtubesearchpython import CustomSearch, VideoSortOrder
from bs4 import BeautifulSoup
import requests
import random


def get_random_number():
    number = random.randrange(0, 10000)
    if number >= 1000:
        return str(number)
    if number >= 100:
        return "0" + str(number)
    if number >= 10:
        return "00" + str(number)
    if number >= 0:
        return "000" + str(number)


def get_random_img_search():
    if random.choice([True, False]):
        return "IMG " + get_random_number()
    else:
        return "IMG_" + get_random_number()


def get_random_web_search():
    if random.choice([True, True, False]):
        if random.choice([True, False]):
            r = requests.get("https://en.wikipedia.org/wiki/Main_Page")
            soup = BeautifulSoup(r.content, "html.parser")
            words = []
            for i in str(soup.text).split():
                if i in words:
                    continue
                words.append(i)
            return random.choice(words)
        else:
            r = requests.get(
                "https://en.m.wikipedia.org/wiki/Special:Random#/random")
            soup = BeautifulSoup(r.content, "html.parser")
            return random.choice(str(soup.text).split())
    else:
        r = requests.get("https://www.bbc.com/")
        soup = BeautifulSoup(r.content, "html.parser")
        words = []
        for i in soup.find_all("a"):
            if not i.get("class"):
                continue
            if "media__link" in i.get("class"):
                if not i.text.strip() in words:
                    words.append(i.text.strip())
        return random.choice(words)


def get_random_video_id():
    while True:
        if random.choice([True, False]):
            word = get_random_img_search()
        else:
            try:
                word = get_random_web_search()
            except:
                word = get_random_img_search()

        if random.choice([True, False]):
            results = YoutubeSearch(word, max_results=100).to_dict()
            video_ids = []
            for i in results:
                video_ids.append(i["id"])
        else:
            customSearch = CustomSearch(
                'img', VideoSortOrder.uploadDate, limit=100)
            video_ids = []
            for i in customSearch.result().get("result"):
                video_ids.append(i.get("id"))

        id = None
        try:
            id = random.choice(video_ids)
            break
        except IndexError:
            continue

    return id


def get_last_version():
    r = requests.get("https://github.com/aenosinc/RandomVideo/releases")
    soup = BeautifulSoup(r.content, "html.parser")
    for i in soup.find_all("span"):
        if not i.get("class"):
            continue
        if "css-truncate-target" in i.get("class"):
            return str(i.text.split("v")[1])


if __name__ == "__main__":
    print(get_last_version(), get_random_video_id())
