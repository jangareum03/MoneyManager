window.onload = function() {
    kakao.maps.load(function() {
        let markers = [];
        let mapContainer = document.getElementById('map'),
            mapOption = {
                center: new kakao.maps.LatLng(33.450701, 126.570667),
                level: 3
            };

        //지도 생성
        let map = new kakao.maps.Map(mapContainer, mapOption);

        //장소검색
        let ps = new kakao.maps.services.Places();

        //검색 결과 목록 및 장소명 표출
        let infowindow = new kakao.maps.InfoWindow({zIndex:1});

        //키워드로 장소 검색
        document.querySelector('#searchBtn').addEventListener('click', function() {
            event.preventDefault();
            searchPlaces();
        });
        searchPlaces();
        function searchPlaces() {
            let keyword = document.getElementById('keyword').value;

            if( !keyword.replace( /^\s+|\s+$/g, '' ) ) {
                alert('키워드를 입력해주시길 바랍니다.');
                return false;
            }

            ps.keywordSearch( keyword, placesSearchCB );
        }

        //장소 검색 후 호출
        function placesSearchCB(data, status, pagination) {
            if( status === kakao.maps.services.Status.OK ) {
                displayPlaces(data);
                displayPagination(pagination);
            }else if( status === kakao.maps.services.Status.ZERO_RESULT ) {
                alert( '장소가 존재하지 않습니다.');
                return;
            }else if( status === kakao.maps.services.Status.ERROR ) {
                alert('검색 결과 중 오류가 발생했습니다.');
                return;
            }
        }

        //검색 결과 목록과 마커 표출
        function displayPlaces(places) {
            let listEl  = document.getElementById('placesList'),
                menuEl = document.getElementById('menu_wrap'),
                fragment = document.createDocumentFragment(),
                bounds = new kakao.maps.LatLngBounds(),
                listStr = '';

            //검색 결과 항목에 추가된 항목 제거
            removeAllChildNods(listEl);

            //지도에 표시된 마커 제거
            removeMarker();

            for( let i=0; i<places.length; i++ ) {
                //마커 생성 및 지도 표시
                let placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
                        marker = addMarker(placePosition, i),
                        itemEl = getListItem(i, places[i]);

                bounds.extend(placePosition);

                //검색 결과 항목에 mouseover 및 mouseout 이벤트 처리
                (function(marker, title) {
                    kakao.maps.event.addListener(marker, 'mouseover', function() {
                        displayInfowindow(marker, title);
                    });

                    kakao.maps.event.addListener(marker, 'mouesout', function() {
                        infowindow.close();
                    });

                    itemEl.onmouseover = function(){
                        displayInfowindow(marker, title);
                    };

                    itemEl.onmouseout = function() {
                        infowindow.close();
                    };
                })(marker, places[i].place_name);

                fragment.appendChild(itemEl);
            }

                // 검색결과 항목들을 검색결과 목록 Element에 추가
                listEl.appendChild(fragment);
                menuEl.scrollTop = 0;

                // 검색된 장소 위치를 기준으로 지도 범위를 재설정
                map.setBounds(bounds);
        }

        // 검색결과 항목을 Element로 반환
        function getListItem(index, places) {
            let el = document.createElement('li');
            el.className = 'item';

            //마커 아이콘
            let markerSpan = document.createElement('span');
            markerSpan.className = `markerbg marker_${index + 1}`;

            //정보 div
            let infoDiv = document.createElement('div');
            infoDiv.className = 'info';

            //장소이름 h5
            let h5 = document.createElement('h5');
            h5.textContent = places.place_name;

            //도로명 주소
            let roadAdd = document.createElement('span');
            let add = document.createElement('span');

            if( places.road_address_name ) {
                roadAdd.textContent = places.road_address_name;

                add.className = 'jibun gray';
                add.textContent = places.address_name;
            }else {
                add.textContent = places.address_name;
            }

            //전화번호 span
            let phoneSpan = document.createElement('span');
            phoneSpan.className = 'tel';
            phoneSpan.textContent = places.phone;


            infoDiv.dataset.placeName = places.place_name;
            infoDiv.dataset.roadAddress = places.road_address_name;
            infoDiv.dataset.address = places.address_name;

            infoDiv.addEventListener('click', function() {
                saveLocation( this.dataset.placeName, infoDiv.dataset.roadAddress, infoDiv.dataset.address );
            });


            infoDiv.appendChild(h5);
            infoDiv.appendChild(add);

            if( places.road_address_name ) {
                infoDiv.appendChild(roadAdd);
            }

            infoDiv.appendChild(phoneSpan);

            el.appendChild(markerSpan);
            el.appendChild(infoDiv);

            return el;
        }

        // 마커를 생성하고 지도 위에 마커를 표시
        function addMarker(position, idx, title) {
            let imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지 사용
                imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
                imgOptions =  {
                    spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
                    spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
                    offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
                },
                markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
                    marker = new kakao.maps.Marker({
                    position: position, // 마커의 위치
                    image: markerImage
                });

            marker.setMap(map);
            markers.push(marker);

            return marker;
        }

        // 지도 위에 표시되고 있는 마커를 모두 제거
        function removeMarker() {
            for ( let i = 0; i < markers.length; i++ ) {
                markers[i].setMap(null);
            }
            markers = [];
        }

        // 검색결과 목록 하단에 페이지번호를 표시
        function displayPagination(pagination) {
            let paginationEl = document.getElementById('pagination'),
                fragment = document.createDocumentFragment(),
                i;

            // 기존에 추가된 페이지번호를 삭제
            while (paginationEl.hasChildNodes()) {
                paginationEl.removeChild (paginationEl.lastChild);
            }

            for (i=1; i<=pagination.last; i++) {
                let el = document.createElement('a');
                el.href = "#";
                el.innerHTML = i;

                if (i===pagination.current) {
                    el.className = 'on';
                } else {
                    el.onclick = (function(i) {
                        return function() {
                            pagination.gotoPage(i);
                        }
                    })(i);
                }

                fragment.appendChild(el);
            }
            paginationEl.appendChild(fragment);
        }

        // 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수
        // 인포윈도우에 장소명을 표시
        function displayInfowindow(marker, title) {
            let content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

            infowindow.setContent(content);
            infowindow.open(map, marker);
        }

         // 검색결과 목록의 자식 Element를 제거하는 함수
        function removeAllChildNods(el) {
            while (el.hasChildNodes()) {
                el.removeChild (el.lastChild);
            }
        }

        //검색 항목 onclick 이벤트 처리
        function saveLocation( name, roadAddress, address ) {
            const container = opener.document.querySelector('.form__input-container--location');
            if( !container ) return;

            //위치정보가 없는 경우
            const locationBox = container.querySelector('.form__input-box');
            if( locationBox.classList.contains('form__input-box--empty') ) {
                //클래스 이름 변경
                locationBox.classList.remove('form__input-box--empty');
                locationBox.classList.add('form__input-box--filled');

                locationBox.replaceChildren();

                const nameLocation = createSpan( {parent: locationBox, classList: ['location__name'], text: name } );             //장소명
                const roadLocation = createSpan( {parent: locationBox, classList: ['location__road'], text: roadAddress } );      //도로주소
                const addSpan = createSpan( {parent: locationBox, classList: ['location__add'], text: address } );                        //지번주소
            }

            //위치정보가 있는 경우
            if( locationBox.classList.contains('form__input-box--filled') ) {
                //장소정보 변경
                container.querySelector('.location__name').textContent = name;                       //장소명
                container.querySelector('.location__road').textContent = roadAddress;           //도로주소
                container.querySelector('.location__add').textContent = address;                      //지번주소
            }

            window.close();
        }
    })
}