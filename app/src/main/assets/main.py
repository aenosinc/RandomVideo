from youtube_search import YoutubeSearch
import random


def get_random_video_id():
    while True:
        number = random.randrange(1000, 10000)
        word = "IMG " + str(number)

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
