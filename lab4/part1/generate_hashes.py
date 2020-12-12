from part1.generate_passwords import generate_human_passwords
from part1.hash import bcryptHash, md5Hash, sha1Hash
from tqdm import tqdm

if __name__ == '__main__':
    hash_ways = {
        'md5': md5Hash,
        'sha1': sha1Hash,
        'bcrypt': bcryptHash
    }

    for hash_name, hash_func in hash_ways.items():
        passwords = generate_human_passwords()
        if hash_name == 'bcrypt':
            passwords = passwords[:1000]
        hashes = [hash_func(x) for x in tqdm(passwords)]
        with open(hash_name + '.csv', 'w') as f:
            for h in hashes:
                f.write(h + '\n')
