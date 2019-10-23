// Global variable
var data = [];

// Change url api here
var root = 'http://localhost:8080/turnConF/api';
// var root = 'https://turncomu.herokuapp.com/api';


// Create template for each row in table
function row(data, index) {
  var $tr = $('<tr/>');
  $tr.append('<td>' + index + '</td>');
  $tr.append('<td>' + data.name + '</td>');
  $tr.append('<td>' + data.turn + '</td>');
  $tr.append('<td>' + data.turnAll + '</td>');
  $tr.append(data.status === '0'? '<td><a href="#" class="js-changeStatus" data-id="' + data.id + '">Inactive</a></td>' : '<td><a href="#" class="js-changeStatus" data-id="' + data.id + '">Active</a></td>');
  $tr.append('<td>' + data.loginTime + '</td>');
  $tr.append('<td><a href="#viewMore" data-id="' + data.id + '" class="btn btn-success js-viewMore">View more</a></td>');
  $tr.append('<td><a href="#addWorkHis" data-id="' + data.id + '" class="btn btn-success js-addWorkHis">Add</a></td>');
  $tr.append(data.working === '1'? '<td><a href="#" class="js-changeWorking" data-id="' + data.id + '">Working</a></td>' : '<td><a href="#" class="js-changeWorking" data-id="' + data.id + '">Free</a></td>');
  return $tr;
}

// Create template for each row in table work his
function rowWorkHis(data, id) {
  var $tr = $('<tr/>');
  $tr.append('<td>' + data.name + '</td>');
  $tr.append(data.free === '1' ? '<td>Free</td>' : '<td>Count</td>');
  $tr.append('<td>' + data.money + '</td>');
  $tr.append('<td><a href="#" data-workHisId="' + data.id + '" data-id="' + id + '" class="js-edit">Edit</a> - <a href="#" data-workHisId="' + data.id + '" data-id="' + id + '" class="js-delete">Delete</a></td>');
  return $tr;
}

function filterDataById(id) {
  // Filter by id, so it return array 0 or 1 record
  return data.filter(function (d) {
    return d.id === id;
  })[0];
}

function initPopup() {
  $('.js-viewMore').magnificPopup({
    type: 'inline',
    preloader: false,
    callbacks: {
      beforeOpen: function (e) {
        var id = this.st.el.attr('data-id');
        var filtered = filterDataById(id);
        var $table = $('.js-ptable');
        $table.empty();
        $.each(filtered.workHis, function (index, value) {
          $table.append(rowWorkHis(value, id));
        });
        if ($table.children().length === 0) {
          $table.append('<tr><td colspan="4" class="text-center">No data</td></tr>');
        }
      }
    },
  });
  $('.js-addWorkHis').magnificPopup({
    type: 'inline',
    preloader: false,
    callbacks: {
      beforeOpen: function () {
        // Reset inputted
        var $padd = $('.js-paddWorkHis');
        var id = $.magnificPopup.instance.st.el.attr('data-id');
        $padd.find('input').val('');
        $padd.find('input[type=checkbox]').prop('checked', false);

        // Set id of each row to modal
        $padd.find('input[name=id]').val(id);
      }
    },
  });
  $('.js-login').magnificPopup({
	    type: 'inline',
	    preloader: false,
	    callbacks: {
	      beforeOpen: function () {
	      }
	    },
	  });
  // Close modal
  $(document).on('click', '.popup-modal-dismiss', function (e) {
    e.preventDefault();
    $.magnificPopup.close();
  });
}

// Call api to fetch data
function fetchData() {
  $.get(root, function (d) {
    var $table = $('.js-table');
    data = d;
    $table.empty();
    $.each(d, function (index, value) {
      $table.append(row(value, index + 1));
    });
    if ($table.children().length === 0) {
      $table.append('<tr><td colspan="9" class="text-center">No data</td></tr>');
    }
  }).done(function (d) {
    initPopup();
  }).fail(function () {
    console.log('Something went wrong while calling API.');
  });
}

function addUser(name) {
  $.get(root + '/addUser/' +
    name
  ).done(function (d) {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when adding new user.');
  });
}

function addWork(d) {
  $.get(root + '/addGroup/' +
    d.id + '/' +
    d.name + '/' +
    d.money + '/' +
    d.free
  ).done(function (d) {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when adding new work his.');
  });
}

function updWork(d) {
  $.get(root + '/upGroup/' +
    d.id + '/' +
    d.workHisId + '/' +
    d.name + '/' +
    d.money + '/' +
    d.free
  ).done(function (d) {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when updating work his.');
  });
}

function delWork(d) {
  $.get(root + '/delGroup/' +
    d.id + '/' +
    d.workHisId
  ).done(function (d) {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when deleting work his.');
  });
}

function changeStatus(id) {
  $.get(root + '/changeStatus/' +
    id
  ).done(function () {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when changing status.');
  });
}

function changeWorking(id) {
  $.get(root + '/changeWorking/' +
    id
  ).done(function () {
    fetchData();
    $.magnificPopup.instance.close();
  }).fail(function () {
    console.log('Something went wrong when changing working.');
  });
}

function checkWorkHis() {
	  if ($('.js-workHisName').val() && $('.js-workHisMoney').val()) {
	    $('.js-add').prop('disabled', false);
	  } else {
	    $('.js-add').prop('disabled', true);
	  }
	}
$(document).ready(function () {

  var $padd = $('.js-paddWorkHis');

  // Call api load data
  fetchData();

  // Open modal edit work his
  $(document).on('click', '.js-edit', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var $this = $(this);
    var id = $this.attr('data-id');
    var workHisId = $this.attr('data-workHisId');
    $padd.find('input[name=id]').val(id);
    $padd.find('input[name=workHisId]').val(workHisId);
    var filtered = filterDataById(id);
    var workHis = filtered.workHis.filter(function (workHis) { return workHis.id === workHisId })[0];
    $.each(workHis, function (key, value) {
      if (key === 'name' || key === 'money') {
        $padd.find('input[name=' + key + ']').val(value);
      }
      if (key === 'free') {
        $padd.find('input[name=' + key + ']').prop('checked', value === '1' ? true : false);
      }
    });
    $.magnificPopup.open({
      items: {
        src: '.js-paddWorkHis',
      },
      type: 'inline',
    });
  });

  // Collect data and call addWork or updWork depend on having workHisId or
	// not
  // If have workHisId, call updWork
  // If have not workHisId, call addWork
  $('.js-add').on('click', function () {
    var $padd = $('.js-paddWorkHis');
    var d = {
      id: $padd.find('input[name=id]').val(),
      free: $padd.find('input[name=free]').prop('checked') ? "1" : "0",
      workHisId: $padd.find('input[name=workHisId]').val(),
      name: $padd.find('select[name=name]').val()
    };
    $('.js-paddWorkHis').find('input[type=text]').each(function (index, value) {
      var $value = $(value);
      Object.assign(d, {
        [$value.attr('name')]: $value.val()
      });
    });
    if (d.workHisId) {
      updWork(d);
    } else {
      addWork(d);
    }
  })

  // Collect data through "data-id" and call delWork
  $(document).on('click', '.js-delete', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var $this = $(this);
    var id = $this.attr('data-id');
    var workHisId = $this.attr('data-workHisId');
    delWork({
      id: id,
      workHisId: workHisId
    })
  })

  // Collect data through "data-id" and call changeStatus
  $(document).on('click', '.js-changeStatus', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var $this = $(this);
    var id = $this.attr('data-id');
    changeStatus(id);
  });

  // Collect data through "data-id" and call changeWorking
  $(document).on('click', '.js-changeWorking', function (e) {
    e.preventDefault();
    e.stopPropagation();
    var $this = $(this);
    var id = $this.attr('data-id');
    changeWorking(id);
  });

  // Validate user name blank
  $('.js-user').on('keyup', function () {
    $('.js-addUser').prop('disabled', !$(this).val());
  });
  
  // Collect data and call addUser
  $('.js-addUser').on('click', function () {
    var user = $('.js-user').val();
    if (user) {
      addUser(user);
    }
  })
  // Collect data and call addUser
  $('.js-LoUser').on('click', function () {
	  $.get(root + '/login/zainab/letmein'
	  ).done(function (d) {
		  console.log('OK');
	  }).fail(function () {
	    console.log('Something went wrong when adding new user.');
	  });
  })
});
