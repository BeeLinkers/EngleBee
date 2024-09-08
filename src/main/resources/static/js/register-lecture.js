$(function(){



  $("#register-lecture-button").click(function(){
    let wordLevel = $("#wordLevel").val();
    let senetenceLevel = $("#sentenceLevel").val();
    let grammerLevel = $("#grammerLevel").val();
    let studentNickname = $("#student-nickname").val();
    let lectureTitle = $("#lecture-title").val();

    let subjectLevels = [
      {
        "subject": "단어",
        "level": wordLevel
      },
      {
        "subject": "문장",
        "level": senetenceLevel
      },
      {
        "subject": "어법",
        "level": grammerLevel
      }
    ];

    let data = JSON.stringify({
      "studentNickname": studentNickname,
      "letureTitle" : lectureTitle,
      "subjectLevels" : subjectLevels
    })

    if(!studentNickname){
      alert("학생 닉네임을 입력하세요");
    }else if(!lectureTitle){
      alert("수업명을 입력하세요");
    }else{

      $.ajax({
        url: '/api/teacher/lecture/register',
        method: 'POST',
        contentType: "application/json",
        data : data
        ,
        success: function (data) {
          console.log("성공");
          alert("수업 생성되었습니다")
          window.location.href = '/main';
        },

        error: function (jqXHR, textStatus, errorThrown) {
          console.error('[ERROR] 데이터를 불러오던 도중 에러가 발생했습니다');
          alert(jqXHR.responseText);
        }
      });

    }



  })


})