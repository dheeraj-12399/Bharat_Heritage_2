package com.example.data.datasource

import com.example.data.model.HeritageItem

object HeritageRepository {

    val categories = listOf(
        "All",
        "Monuments",
        "Historic Forts",
        "Sacred Temples",
        "Ancient Caves",
        "UNESCO Sites",
        "Living Traditions"
    )

    val states = listOf(
        "All States",
        "Uttar Pradesh",
        "Rajasthan",
        "Madhya Pradesh",
        "Tamil Nadu",
        "Karnataka",
        "Maharashtra",
        "Odisha",
        "Punjab",
        "Delhi",
        "Bihar"
    )

    val heritageItems: List<HeritageItem> = listOf(
        HeritageItem(
            id = "taj_mahal",
            name = "Taj Mahal",
            hindiName = "ताज महल",
            category = "Monuments",
            state = "Uttar Pradesh",
            city = "Agra",
            period = "1631–1648 CE",
            builtBy = "Emperor Shah Jahan",
            architecturalStyle = "Mughal (Indo-Islamic with Persian & Central Asian influences)",
            description = "An immense mausoleum of white marble on the right bank of the river Yamuna, universally admired as an architectural wonder of the world.",
            history = "Commissioned by the fifth Mughal Emperor Shah Jahan in 1631 in memory of his beloved wife Mumtaz Mahal. Over 20,000 artisans and craftsmen from India, Persia, and the Ottoman Empire worked on this masterpiece, completed around 1648 with secondary buildings continuing until 1653.",
            culturalSignificance = "Symbol of eternal devotion and the pinnacle of symmetry in Indian-Islamic architecture. It showcases pietra dura (parchin kari) stone inlay work with precious gems and verses from the Quran carved in Thuluth script.",
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Changes color throughout the day: pinkish at dawn, radiant white at noon, and golden under the moon.",
                "Its four minarets are tilted slightly outwards to protect the central dome in case of an earthquake.",
                "Recognized as a UNESCO World Heritage Site in 1983."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = true,
            latitude = 27.1751,
            longitude = 78.0421
        ),
        HeritageItem(
            id = "red_fort",
            name = "Red Fort (Lal Qila)",
            hindiName = "लाल क़िला",
            category = "Historic Forts",
            state = "Delhi",
            city = "New Delhi",
            period = "1638–1648 CE",
            builtBy = "Emperor Shah Jahan",
            architecturalStyle = "Mughal Military & Imperial Palace Architecture",
            description = "The historic fortified palace in Old Delhi that served as the main residence of the Mughal Emperors for nearly two centuries.",
            history = "When Shah Jahan decided to shift his capital from Agra to Delhi in 1638, he commissioned the Red Fort (Qila-i-Mubarak) on the banks of the Yamuna River. Construction began on 12 May 1638 under architect Ustad Ahmad Lahori and took a decade to complete.",
            culturalSignificance = "Deeply woven into India's struggle for independence. On 15 August 1947, India's first Prime Minister Pandit Jawaharlal Nehru hoisted the national tricolour above the Lahori Gate, a tradition upheld every Independence Day.",
            imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Originally composed of red sandstone and white lime plaster.",
                "Houses the legendary Diwan-i-Aam and Diwan-i-Khas where the Peacock Throne once stood.",
                "Declared a UNESCO World Heritage Site in 2007."
            ),
            bestTimeToVisit = "November to February",
            isUnesco = true,
            latitude = 28.6562,
            longitude = 77.2410
        ),
        HeritageItem(
            id = "konark_sun_temple",
            name = "Konark Sun Temple",
            hindiName = "कोणार्क सूर्य मंदिर",
            category = "Sacred Temples",
            state = "Odisha",
            city = "Puri District",
            period = "13th Century (c. 1250 CE)",
            builtBy = "King Narasimhadeva I of Eastern Ganga Dynasty",
            architecturalStyle = "Kalinga Architecture",
            description = "A monumental chariot of Surya, the Sun God, with 24 carved stone wheels pulled by seven spirited horses towards the dawn.",
            history = "Constructed around 1250 CE by King Narasimhadeva I to commemorate his military victories. Sailors once called it the 'Black Pagoda' due to its dark tower facade seen from the Bay of Bengal, contrasting with the 'White Pagoda' of Puri Jagannath.",
            culturalSignificance = "An astonishing convergence of sacred geometry, astronomy, and stone sculpting. The 24 wheels function as precise sundials calculating time down to minutes using the shadows cast on their spokes.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "The 12 pairs of wheels represent the 12 months of the Hindu solar calendar.",
                "The 7 horses symbolize the seven days of the week and the seven colors of sunlight.",
                "Featured on the reverse of the Indian 10-rupee currency note."
            ),
            bestTimeToVisit = "September to March",
            isUnesco = true,
            latitude = 19.8876,
            longitude = 86.0945
        ),
        HeritageItem(
            id = "hampi",
            name = "Hampi Monuments",
            hindiName = "हम्पी स्मारक",
            category = "UNESCO Sites",
            state = "Karnataka",
            city = "Vijayanagara",
            period = "14th–16th Century CE",
            builtBy = "Harihara I, Bukka Raya I, and Emperor Krishnadevaraya",
            architecturalStyle = "Vijayanagara Dravidian Architecture",
            description = "The magnificent ruins of the Vijayanagara Empire nestled among boulder-strewn hills on the banks of the sacred Tungabhadra River.",
            history = "Hampi was the thriving capital of the Vijayanagara Empire from 1336 to 1565 CE. At its zenith, it was recorded by Persian and European travelers as one of the world's richest and second-largest medieval cities after Beijing.",
            culturalSignificance = "Associated with the mythical Kishkindha of the Ramayana epic. Home to the iconic Stone Chariot (Garuda Shrine) at the Vittala Temple complex and the active Virupaksha Temple worshipping Lord Shiva.",
            imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e47087ef?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "The Vittala Temple features 56 musical stone pillars that produce melodic resonance when gently tapped.",
                "The Stone Chariot is featured on the reverse side of India's 50-rupee note.",
                "Inscribed as a UNESCO World Heritage Site in 1986."
            ),
            bestTimeToVisit = "October to February",
            isUnesco = true,
            latitude = 15.3350,
            longitude = 76.4600
        ),
        HeritageItem(
            id = "ajanta_caves",
            name = "Ajanta & Ellora Caves",
            hindiName = "अजन्ता और एलोरा गुफाएँ",
            category = "Ancient Caves",
            state = "Maharashtra",
            city = "Chhatrapati Sambhajinagar",
            period = "2nd Century BCE to 10th Century CE",
            builtBy = "Satavahana, Vakataka, and Rashtrakuta Dynasties",
            architecturalStyle = "Ancient Indian Rock-cut Cave Architecture",
            description = "Masterpieces of Buddhist, Hindu, and Jain rock-cut architecture, celebrated worldwide for ancient wall frescoes and the monolithic Kailasa Temple.",
            history = "Ajanta consists of 30 rock-hewn Buddhist caves carved into a horse-shoe shaped cliff overlooking the Waghur River. Ellora contains 34 caves celebrating religious harmony: Hindu, Buddhist, and Jain, highlighted by the monolithic Kailasa Temple carved top-down from a single basalt cliff.",
            culturalSignificance = "Ajanta preserves the world's greatest surviving examples of ancient Indian painting, depicting Jataka tales, Bodhisattvas (Padmapani and Vajrapani), and courtly life with expressive emotive artistry.",
            imageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Kailasa Temple (Cave 16 at Ellora) is the world's largest monolithic rock excavation.",
                "Over 200,000 tonnes of basalt rock were chiseled away over a century to form Kailasa.",
                "Both were among India's first UNESCO World Heritage sites in 1983."
            ),
            bestTimeToVisit = "June to March",
            isUnesco = true,
            latitude = 20.5519,
            longitude = 75.7033
        ),
        HeritageItem(
            id = "qutub_minar",
            name = "Qutub Minar Complex",
            hindiName = "क़ुतुब मीनार",
            category = "Monuments",
            state = "Delhi",
            city = "Mehrauli, New Delhi",
            period = "1199–1220 CE",
            builtBy = "Qutb-ud-din Aibak & Shams-ud-din Iltutmish",
            architecturalStyle = "Indo-Islamic Afghan Architecture",
            description = "The tallest individual brick minaret in the world, rising 72.5 meters with intricately carved Arabic inscriptions and decorative projecting balconies.",
            history = "Construction began in 1199 CE by Qutb-ud-din Aibak, founder of the Delhi Sultanate, and was expanded by his successor Iltutmish. Firoz Shah Tughlaq repaired and added the top white marble stories in 1368 after lightning damage.",
            culturalSignificance = "The courtyard contains the rust-resistant 4th-century Iron Pillar of Chandragupta II (Gupta Empire), a tribute to ancient Indian metallurgy that has withstood the elements for over 1,600 years without rusting.",
            imageUrl = "https://images.unsplash.com/photo-1545231027-637d2f6210f8?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Stands at 72.5 meters (238 feet) with 379 spiral steps inside.",
                "The 6-tonne Iron Pillar contains 98% wrought iron with high phosphorus content preventing oxidation.",
                "Part of a UNESCO World Heritage Complex since 1993."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = true,
            latitude = 28.5245,
            longitude = 77.1855
        ),
        HeritageItem(
            id = "meenakshi_temple",
            name = "Meenakshi Amman Temple",
            hindiName = "मीनाक्षी अम्मन मंदिर",
            category = "Sacred Temples",
            state = "Tamil Nadu",
            city = "Madurai",
            period = "6th Century BCE onwards (rebuilt 1623–1655 CE)",
            builtBy = "Pandya Dynasty & Nayaka King Tirumala Nayaka",
            architecturalStyle = "Dravidian Temple Architecture",
            description = "A historic Hindu temple situated on the southern bank of the Vaigai River, dedicated to Goddess Meenakshi (Parvati) and Sundareswarar (Shiva).",
            history = "Originally mentioned in early Tamil Sangam literature over two millennia ago. The present towering gopurams and vast hall of thousand pillars were built during the Madurai Nayaka period under Vishwanatha and Tirumala Nayaka.",
            culturalSignificance = "The spiritual heart of Madurai. The city is laid out in concentric squares around the temple according to ancient Vastu Shastra rules. It features 14 majestic gateway towers (Gopurams) adorned with thousands of colorful mythological stucco figures.",
            imageUrl = "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Features the renowned Hall of 1000 Pillars (Aayiram Kaal Mandapam) displaying 985 intricately sculpted granite pillars.",
                "Outside the hall stand the musical pillars that produce different swaras (notes) when struck.",
                "Attracts over 15,000 visitors daily and up to 25,000 during the annual Chithirai Festival."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = false,
            latitude = 9.9195,
            longitude = 78.1193
        ),
        HeritageItem(
            id = "amer_fort",
            name = "Amer Fort & Palace",
            hindiName = "आमेर का क़िला",
            category = "Historic Forts",
            state = "Rajasthan",
            city = "Jaipur",
            period = "1592 CE",
            builtBy = "Raja Man Singh I",
            architecturalStyle = "Rajput & Mughal Fusion Architecture",
            description = "A majestic hilltop fortress crafted from yellow and pink sandstone, overlooking the tranquil Maota Lake in the Aravalli hills.",
            history = "Principal stronghold of the Kachwaha Rajputs before Maharaja Sawai Jai Singh II founded Jaipur city in 1727. Raja Man Singh I began construction in 1592, and successive rulers added the Diwan-e-Aam, Sukh Niwas, and the world-famous Sheesh Mahal.",
            culturalSignificance = "Celebrated for the Sheesh Mahal (Mirror Palace) where thousands of concave Belgian convex mirror mosaics illuminate the entire chamber with the light of a single candle.",
            imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Connected by an underground subterranean escape tunnel to the military Jaigarh Fort.",
                "Features an ingenious ancient Persian wheel water system pulling water up from Maota Lake.",
                "Inscribed as part of the Hill Forts of Rajasthan UNESCO World Heritage Site in 2013."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = true,
            latitude = 26.9855,
            longitude = 75.8513
        ),
        HeritageItem(
            id = "varanasi_ghats",
            name = "Varanasi Heritage Ghats",
            hindiName = "वाराणसी के प्राचीन घाट",
            category = "Living Traditions",
            state = "Uttar Pradesh",
            city = "Varanasi (Kashi)",
            period = "Continuous for over 3,000 years",
            builtBy = "Marathas, Holkars, Scindias, and Royal Houses of India",
            architecturalStyle = "Traditional Riverfront Tiered Ghat Architecture",
            description = "The spiritual heart of Sanatana Dharma, featuring 84 stone riverfront ghats lining the sacred crescent of the holy Ganga.",
            history = "One of the oldest continuously inhabited cities on Earth, celebrated by Mark Twain: 'Older than history, older than tradition, older even than legend, and looks twice as old as all of them put together.' The iconic ghats were refurbished with stone riverfront palaces in the 18th century.",
            culturalSignificance = "Every evening at Dashashwamedh Ghat, priests perform the spectacular Ganga Aarti with tiered brass lamps, incense, conch shells, and sacred Vedic chants as thousands of diya lanterns float downstream.",
            imageUrl = "https://images.unsplash.com/photo-1561359313-0639aad49ca6?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Kashi Vishwanath Temple on the riverfront is one of the 12 sacred Jyotirlingas.",
                "The Manikarnika and Harishchandra Ghats have maintained uninterrupted sacred cremation fires for millennia.",
                "Celebrated center for classical Hindustani music, philosophy, and Banarasi silk weaving."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = false,
            latitude = 25.3076,
            longitude = 83.0107
        ),
        HeritageItem(
            id = "khajuraho",
            name = "Khajuraho Group of Monuments",
            hindiName = "खजुराहो स्मारक समूह",
            category = "UNESCO Sites",
            state = "Madhya Pradesh",
            city = "Chhatarpur District",
            period = "950–1050 CE",
            builtBy = "Chandela Dynasty",
            architecturalStyle = "Nagara Style Temple Architecture",
            description = "A stunning cluster of Hindu and Jain temples renowned for architectural harmony, soaring curvilinear spires, and intricate stone sculptures celebrating all dimensions of life.",
            history = "Built over a century by the kings of the Chandela dynasty. Of the original 85 temples, about 25 have survived, protected by dense forests until rediscovered in the 1830s by British surveyor T.S. Burt.",
            culturalSignificance = "The temples, including the grand Kandariya Mahadeva Temple, celebrate the four Purusharthas (Dharma, Artha, Kama, and Moksha). The sculptures showcase everyday life, musicians, celestial dancers (Apsaras), warfare, and devotion.",
            imageUrl = "https://images.unsplash.com/photo-1620619767323-b95a89183081?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Kandariya Mahadeva Temple rises like Mount Kailash with 84 miniature spires surrounding the main tower.",
                "Erotic carvings comprise less than 10% of the artwork, symbolizing liberation and sacred union.",
                "Recognized as a UNESCO World Heritage Site in 1986."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = true,
            latitude = 24.8318,
            longitude = 79.9199
        ),
        HeritageItem(
            id = "golden_temple",
            name = "Sri Harmandir Sahib (Golden Temple)",
            hindiName = "श्री हरिमंदिर साहिब",
            category = "Sacred Temples",
            state = "Punjab",
            city = "Amritsar",
            period = "1588–1604 CE",
            builtBy = "Guru Arjan Dev Ji (gold plating by Maharaja Ranjit Singh)",
            architecturalStyle = "Sikh Architecture (Synthesis of Indo-Islamic & Hindu styles)",
            description = "The holiest Gurdwara of Sikhism, situated in the center of the holy Amrit Sarovar lake, plated with pure gold leaf.",
            history = "Fifth Sikh Guru, Guru Arjan Dev Ji, designed the gurdwara with four entrances open to all four castes and directions. The foundation stone was laid by Sufi saint Hazrat Mian Mir in 1588. In 1830, Maharaja Ranjit Singh overlaid the sanctum with copper and 750 kg of pure gold leaf.",
            culturalSignificance = "Operates the world's largest free community kitchen (Guru Ka Langar), serving nutritious hot meals to over 100,000 visitors every day regardless of religion, caste, gender, or background.",
            imageUrl = "https://images.unsplash.com/photo-1588096344356-9b441f71a067?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Built on a lower level than surrounding land so devotees must step down in humility to enter.",
                "Houses the original Adi Granth installed by Guru Arjan Dev Ji in 1604.",
                "The sacred pond (Amrit Sarovar) was excavated under the fourth Sikh Guru, Guru Ram Das."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = false,
            latitude = 31.6200,
            longitude = 74.8765
        ),
        HeritageItem(
            id = "nalanda",
            name = "Nalanda Mahavihara",
            hindiName = "नालंदा महाविहार",
            category = "UNESCO Sites",
            state = "Bihar",
            city = "Nalanda",
            period = "5th to 12th Century CE",
            builtBy = "Gupta Empire (Emperor Kumaragupta I)",
            architecturalStyle = "Ancient Monastic University Architecture",
            description = "The ruins of the world's most renowned ancient residential university and Buddhist monastery, which instructed over 10,000 scholars from across Asia.",
            history = "Flourished from 427 CE to 1197 CE. Attracted scholars from Tibet, China, Korea, Central Asia, and Sri Lanka, including famed pilgrim travelers Xuanzang and Yijing. Courses spanned astronomy, mathematics, medicine, linguistics, and philosophy.",
            culturalSignificance = "The university's celebrated nine-story library, Dharmaganja, housed millions of ancient manuscripts. Aryabhata, the pioneer of zero and planetary motion, is believed to have led the university.",
            imageUrl = "https://images.unsplash.com/photo-1596178065887-1198b6148b2b?w=800&auto=format&fit=crop&q=80",
            facts = listOf(
                "Main Stupa (Sariputra Stupa) features seven layers of construction built sequentially over centuries.",
                "Had strict entrance examinations conducted by gatekeeper professors where only 20% passed.",
                "Declared a UNESCO World Heritage Site in 2016."
            ),
            bestTimeToVisit = "October to March",
            isUnesco = true,
            latitude = 25.1357,
            longitude = 85.4452
        )
    )

    fun getById(id: String): HeritageItem? {
        return heritageItems.find { it.id == id }
    }

    fun search(query: String, selectedCategory: String = "All", selectedState: String = "All States"): List<HeritageItem> {
        val q = query.trim().lowercase()
        return heritageItems.filter { item ->
            val matchesCategory = (selectedCategory == "All" || item.category == selectedCategory)
            val matchesState = (selectedState == "All States" || item.state == selectedState)
            val matchesQuery = q.isEmpty() ||
                item.name.lowercase().contains(q) ||
                item.hindiName.lowercase().contains(q) ||
                item.city.lowercase().contains(q) ||
                item.state.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.architecturalStyle.lowercase().contains(q) ||
                item.builtBy.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.culturalSignificance.lowercase().contains(q) ||
                item.history.lowercase().contains(q)

            matchesCategory && matchesState && matchesQuery
        }
    }
}
