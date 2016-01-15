var ciphertext = "KLUSISWEURQFGVAVQAXEFOCTYRGTBBTVMCSSZVETWSRPXSCODMFSKBEWANQTESDIVTYMEDWHSXEYWHRRPIUVXLFGWSFVMSBEFPXAZBLRPAHBFELOWBLVOOZFZGMNAWRAPRWCJATYOBKSMZWBNMFHIWRVPIVQRGOLQZRXMTQBENGSBSZJFYGNIHEAENPXTIVXFXFADVRRUTPNKWALQPZXARABSSARQFYEETWYZKTTCCYMECQTRVUNIMFSAKILFOMYANPWATBNMMMNQJZXTAKBDTXIIAKWUGPLFYSOBBRDAOIAUMXLIFBEDOCAUEFAPNEHRUTBWLASXVKEXSBBWMZDWHKMRAVLSSPYPNJFDOCTYXTIUVEKAOLGYMZKQAXWMYANDEGRGVCPVOQALTIIBUPSGTWAZKTTBBUMECCFJSGRNVEHUNOFDMPNQTYXMTUNOMYSEURXPOGBLWMYTVGTUSVGRWBRQPPVUGPGCMBPQGZWFHCFRQMUZLKVATABWJFOXNIMESEBIPPFIZFYEZWBGLATWTIEBHANJETAZNCMANIAUGMUBVFYELGURRPSIPRRPYJNIXAAKUZQBTPNKLMSBUISINIGNMSABUZQPC", letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""), plaintext = '', nat_freq = { A: 8.167, B: 1.492, C: 2.782, D: 4.253, E: 12.702, F: 2.228, G: 2.015, H: 6.094, I: 6.966, J: 0.153, K: 0.772, L: 4.025, M: 2.406, N: 6.749, O: 7.507, P: 1.929, Q: 0.095, R: 5.987, S: 6.327, T: 9.056, U: 2.758, V: 0.978, W: 2.361, X: 0.150, Y: 1.974, Z: 0.074 }, cipher_spacing = {}, key_lens = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], key = [], key_len = 0, key_max = 0;

// decrypts letter
function subtract(a, b) {
    return letters[(26 + letters.indexOf(a) - letters.indexOf(b)) % 26];
}

// create an array of positions for every three-letter sequence in the ciphertext
for (var a = 0; a < ciphertext.length - 2; a++) {
    var sub_str = ciphertext.substring(a, a+3); // three-letter sequence

    if (!(sub_str in cipher_spacing)) {
        cipher_spacing[sub_str] = [];
    }

    cipher_spacing[sub_str].push(a);  // add position of this sequence to array
}

// calculate possible key lengths given spacing between three-letter sequences
for (var a in cipher_spacing) {
    var c = cipher_spacing[a];
    // only check for sequences that appear more than once
    if (c.length > 1) {
        for (var b = 0; b < c.length - 1; b++) {
            // check divisibility for spacing from lengths 4 to 10 between subsequent positions
            for (var d = 4; d <= 10; d++) {
                if ((c[b + 1] - c[b]) % d == 0) {
                    key_lens[d]++;
                }
            }
        }
    }
}

// figure out max 
for (var a = 0; a < key_lens.length; a++) {
    if (key_max < key_lens[a]) {
        key_len = a;
        key_max = key_lens[a];
    }
}

// btw key length turns out to be 6

// once key length is determined, align letter guesses by least square difference over letter frequency
for (var i = 0; i < key_len; i++) {
    var min_sq_diff = 260001, min_letter = '';

    for (var l = 0; l < letters.length; l++) {
        var current_freq = { A: 0, B: 0, C: 0, D: 0, E: 0, F: 0, G: 0, H: 0, I: 0, J: 0, K: 0, L: 0, M: 0, N: 0, O: 0, P: 0, Q: 0, R: 0, S: 0, T: 0, U: 0, V: 0, W: 0, X: 0, Y: 0, Z: 0 },
            letter = letters[l], sq_diff = 0;

        // increment the frequency counter
        for (var j = 0; j < Math.floor(ciphertext.length/key_len); j++) {
            current_freq[subtract(ciphertext.charAt(j * key_len + i), letter)]++;
        }

        // normalize values and multiple by 100 for percentage form
        for (var q in current_freq) {
            current_freq[q] = current_freq[q] * 100 / 122;
        }

        // calculate square difference between frequencies
        for (var q in current_freq) {
            sq_diff += (nat_freq[q] - current_freq[q]) * (nat_freq[q] - current_freq[q]);
        }

        // update letter/value if its the new minimum
        if (min_sq_diff > sq_diff) {
            min_sq_diff = sq_diff;
            min_letter = letter;
        }
    }

    // add the best possible letter to the key, the best being the one that creates the least square difference
    key[i] = min_letter;
}

// the key turns out to be 'REMAIN'

// decrypt ciphertext to plaintext given key
for (var i = 0; i < ciphertext.length; i++) {
    plaintext += subtract(ciphertext.charAt(i), key[i%key_len]);
}

console.log('key: ' + key.join(""));
console.log('plaintext: ' + plaintext);

// you can run this in chrome if you just open the developer panel and copy-paste-run it in the console